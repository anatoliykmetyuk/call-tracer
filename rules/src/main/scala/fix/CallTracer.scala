package fix

import scalafix.v1._
import scala.meta._
import java.io.{ File, PrintWriter }
import scala.util.matching.Regex


object CallTracerMode {
  val record = "record"
  val visit  = "visit"
}

case class CallTracerConfig(
  targets        : Set[String]              = Set.empty
, mode           : String                   = CallTracerMode.record
, excludedArgs   : Set[String]              = Set.empty
, excludedArgsFor: Map[String, Set[String]] = Map.empty
, libraryLocation: String                   = "library.scala"
, signatureFile  : String                   = "signatures.txt")

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

  override def fix(implicit doc: SyntacticDocument): Patch = doc.tree.collect {
    case t: Defn.Def if targets(t.name.value) =>
      val excluded = excludedArgs ++ excludedArgsFor.getOrElse(t.name.value, Set.empty)
      val args     = t.paramss.flatten.map(_.name.value).filterNot(excluded)
      Patch.addLeft(t.body, s"{ calltracer.trace(calltracer.currentStackFrame, ${args.mkString(", ")}); ") + Patch.addRight(t.body, " }")
  }.asPatch

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
    val libraryTemplate = scala.io.Source.fromResource("calltracer.scala").mkString
    val variableRegex = """#\{([\w\d_-]+)\}""".r
    variableRegex.replaceAllIn(libraryTemplate,
      m => m.group(1) match {
        case "mode"          => mode
        case "signatureFile" => signatureFile
        case _ => m.toString })
  }
}
