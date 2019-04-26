import java.io.{ File, PrintWriter }

package object calltracer {
  
  val signatureFile = new File("#{signatureFile}")
  val writer = new PrintWriter(signatureFile)

  def trace(frame: String) = writer.println(frame)

  def dump() = { println(s"Terminating the printer: ${signatureFile}"); writer.flush(); writer.close() }
}
