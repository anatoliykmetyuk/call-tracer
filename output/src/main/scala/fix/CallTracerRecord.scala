package fix

object CallTracerRecord {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.record(bar, stuff, x); stuff }
  def g(x: Int, y: Double) = { calltracer.record(x); {
    println(s"This is a method with a body")
    x
  } }

}
