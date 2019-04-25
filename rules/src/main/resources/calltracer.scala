import java.io.{ File, PrintWriter }

package object calltracer {
  sealed trait Tracer { def apply(method: StackTraceElement, args: Any*): Unit }

  class Recorder extends Tracer {
    private[this] var recorded: List[(StackTraceElement, String)] = List.empty

    def apply(method: StackTraceElement, args: Any*): Unit = {
      val sign = signature(method, args: _*)
      if  (recorded.exists(_._1 == method)) recorded = recorded.map { case (m, _) if m == method => m -> sign case x => x }
      else recorded :+= method -> sign
    }
  
    def dump(file: File) = {
      val signatures = recorded.map(_._2).mkString("\n")
      if (!file.exists) {
        val writer = new PrintWriter(file)
        writer.write(signatures)
        writer.close()
      }
    }
  }

  class Visitor extends Tracer {
    lazy val recorded = scala.io.Source.fromFile(signatureFile).getLines.toList
    private[this] var visited: List[StackTraceElement] = List.empty

    def apply(method: StackTraceElement, args: Any*): Unit =
      if (recorded contains signature(method, args: _*)) visited :+= method

    sys.addShutdownHook { println(s"Visited stack trace:\n${visited.mkString("\n")}") }
  }

  val mode          = "#{mode}"
  val signatureFile = "#{signatureFile}"
  val trace: Tracer = if (mode == "record") new Recorder else new Visitor

  def dump = trace match {
    case rec: Recorder => rec.dump(new File(signatureFile))
    case _ => throw new RuntimeException("The `dump` method is available only in the 'record' mode")
  }

  def signature(method: StackTraceElement, args: Any*): String =
    s"${method}: ${args.mkString(", ")}"

  def currentStackFrame: StackTraceElement = Thread.currentThread.getStackTrace.toList match {
    case getStackTrace :: currentStackFrame :: target :: _ => target }
}
