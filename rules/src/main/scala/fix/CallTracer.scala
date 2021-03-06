package fix

import scalafix.v1._
import scala.meta._, scala.meta.inputs.Input.VirtualFile
import java.io.{ File, PrintWriter }
import scala.util.matching.Regex


case class CallTracerConfig(
  libraryLocation: String        = "library.scala"
, signatureFile  : String        = "signatures.txt"
, libraryLink    : String        = "file:////Users/anatolii/Projects/dotty/calltracer/rules/src/main/resources/calltracer.scala")
case class ArgRule(forType: List[String] = Nil, action: String = "x")

object ArgRule {
  def default = ArgRule()
  implicit val surface = metaconfig.generic.deriveSurface[ArgRule]
  implicit val decoder = metaconfig.generic.deriveDecoder(default)
}

object CallTracerConfig {
  def default = CallTracerConfig()
  implicit val surface = metaconfig.generic.deriveSurface[CallTracerConfig]
  implicit val decoder = metaconfig.generic.deriveDecoder(default)
}

class CallTracer(config: CallTracerConfig) extends SyntacticRule("CallTracer") {
  import config._

  def this() = this(CallTracerConfig.default)

  override def withConfiguration(config: Configuration) =
    config.conf
      .getOrElse("CallTracer")(this.config)
      .map { newConfig => new CallTracer(newConfig) }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def =>
        val args = t.paramss.flatten.map(_.name.value).map { name => s""""$name" -> $name""" } match {
          case xs if xs.nonEmpty => ", " + xs.mkString(", ")
          case _ => ""
        }
        val path      = doc.input.asInstanceOf[VirtualFile].path
        val position  = s"${t.name.pos.startLine+1}:${t.name.pos.startColumn+1}"
        val name      = t.name.value

        val tracer    = s"""{ calltracer.trace("$name", "$path", "$position"$args); """
        Patch.addLeft(t.body, tracer) + Patch.addRight(t.body, " }")
    }.asPatch
  }

  override def beforeStart(): Unit = {
    val libraryContents = loadLibrary()
    val targetFile      = new File(libraryLocation)
    if (!targetFile.exists) {
      val writer = new PrintWriter(targetFile)
      writer.write(libraryContents)
      writer.close()
    }
  }

  def loadLibrary(): String = {
    val libraryTemplate = scala.io.Source.fromURL(libraryLink).mkString
    val variableRegex = """#\{([\w\d_-]+)\}""".r
    variableRegex.replaceAllIn(libraryTemplate,
      m => m.group(1) match {
        case "signatureFile" => signatureFile
        case _ => m.toString })
  }
}
