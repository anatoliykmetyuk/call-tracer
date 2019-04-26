import java.io.{ File, PrintWriter }

package object calltracer {

  val signatureFile = "#{signatureFile}"

  val log = scala.collection.mutable.ListBuffer[String]()

  def trace(frame: String) = log += frame

  def dump() = {
    val writer = new PrintWriter(new File(signatureFile))
    writer.write(log.mkString("\n"))
    writer.close()
  }
}
