import java.io.{ File, PrintWriter }

package object calltracer {
  sealed trait Tracer { def apply(method: StackTraceElement, args: Any*): Unit }

  class Recorder extends Tracer {
    private[calltracer] var _recorded: List[(StackTraceElement, String)] = List.empty

    def recorded = _recorded

    def apply(method: StackTraceElement, args: Any*): Unit = {
      val sign = signature(method, args: _*)
      if  (_recorded.exists(_._1 == method)) _recorded = _recorded.map { case (m, _) if m == method => m -> sign case x => x }
      else _recorded :+= method -> sign
    }
  }

  class Visitor extends Tracer {
    lazy val recorded = scala.io.Source.fromFile(signatureFile).getLines.toList
    private[calltracer] var visited: List[StackTraceElement] = List.empty

    def apply(method: StackTraceElement, args: Any*): Unit =
      if (recorded contains signature(method, args: _*)) visited :+= method
  }

  val mode          = "#{mode}"
  val signatureFile = "#{signatureFile}"

  val trace   : Tracer   = if (mode == "record") new Recorder else new Visitor
  def recorder: Recorder = trace.asInstanceOf[Recorder]
  def visitor : Visitor  = trace.asInstanceOf[Visitor ]

  def dumpRecorded(recorded: List[(StackTraceElement, String)]) = {
    val signatures = recorded.map(_._2).mkString("\n")
    println(s"Recorded stack trace:\n${signatures}")
    val writer = new PrintWriter(new File(signatureFile))
    writer.write(signatures)
    writer.close()
  }

  def dumpVisited = trace match {
    case rec: Recorder => throw new RuntimeException(s"`dumpVisited` is available only in the 'visit' mode of operation")
    case vis: Visitor  => println(s"Visited stack trace:\n${vis.visited.mkString("\n")}")
  }

  def signature(method: StackTraceElement, args: Any*): String =
    s"${method}: ${args.mkString(", ")}"

  def currentStackFrame: StackTraceElement = Thread.currentThread.getStackTrace.toList match {
    case getStackTrace :: currentStackFrame :: target :: _ => target
    case x => throw new RuntimeException(s"Calltracer cannot analyse the following stack trace:\n${x.map("  " + _).mkString("\n")}") }
}
