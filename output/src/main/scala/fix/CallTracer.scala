package fix

object CallTracer {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.traceCall(bar, stuff, x); stuff }
  def g(x: Int, y: Double) = { calltracer.traceCall(x); {
    println(s"This is a method with a body")
    x
  } }

}
