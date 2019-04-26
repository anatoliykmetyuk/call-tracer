import java.io.{ File, PrintWriter }

package object calltracer {

  val signatureFile = "signatures.txt"

  val log = scala.collection.mutable.ListBuffer[StackTraceElement]()

  def trace(frame: StackTraceElement) = log += frame

  def dump() = {
    val writer = new PrintWriter(new File(signatureFile))
    writer.write(log.mkString("\n"))
    writer.close()
  }

  def currentStackFrame: StackTraceElement = Thread.currentThread.getStackTrace.toList match {
    case getStackTrace :: currentStackFrame :: target :: _ => target
    case x => throw new RuntimeException(s"Calltracer cannot analyse the following stack trace:\n${x.map("  " + _).mkString("\n")}") }
}
