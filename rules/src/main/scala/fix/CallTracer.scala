package fix

import scalafix.v1._
import scala.meta._
import java.io.{ File, PrintWriter }


object CallTracerMode {
  val record = "record"
  val visit  = "visit"
}

case class CallTracerConfig(
  targets        : Set[String]              = Set.empty
, mode           : String                   = CallTracerMode.record
, excludedArgs   : Set[String]              = Set.empty
, excludedArgsFor: Map[String, Set[String]] = Map.empty
, libraryLocation: String                   = "/dev/null")

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
      Patch.addLeft(t.body, s"{ calltracer.${mode}(${args.mkString(", ")}); ") + Patch.addRight(t.body, " }")
  }.asPatch

  override def beforeStart(): Unit = {
    val targetFile = new File(libraryLocation)
    val libraryContents = scala.io.Source.fromResource("calltracer.scala").mkString
    if (!targetFile.exists) {
      val writer = new PrintWriter(targetFile)
      writer.write(libraryContents)
      writer.close()
    }
  }
}
