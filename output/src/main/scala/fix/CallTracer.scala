package fix

object CallTracer {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.trace(calltracer.currentStackFrame, bar, stuff, x); stuff }
  def g(x: Int, y: Double) = { calltracer.trace(calltracer.currentStackFrame, x); {
    println(s"This is a method with a body")
    x
  } }

  def f() = { calltracer.trace(calltracer.currentStackFrame); "" }
  def h = { calltracer.trace(calltracer.currentStackFrame); "" }

}