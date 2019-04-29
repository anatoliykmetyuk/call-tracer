import java.io.{ File, PrintWriter }

package object calltracer {
  
  val signatureFile = new File("#{signatureFile}")
  val writer = new PrintWriter(signatureFile)

  def trace(methodName: String, filePath: String, methodPosition: String, args: (String, Any)*) = writer.println(methodName)

  def dump() = { println(s"Terminating the printer: ${signatureFile}"); writer.flush(); writer.close() }
}
