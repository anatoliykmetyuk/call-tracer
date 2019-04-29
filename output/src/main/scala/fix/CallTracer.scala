package fix

object CallTracer {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.trace("foo", "fix/CallTracer.scala", "17:7", "bar" -> bar, "char" -> char, "stuff" -> stuff, "x" -> x, "y" -> y); stuff }
  def g(x: Int, y: Double) = { calltracer.trace("g", "fix/CallTracer.scala", "18:7", "x" -> x, "y" -> y); {
    println(s"This is a method with a body")
    x
  } }

  def f() = { calltracer.trace("f", "fix/CallTracer.scala", "23:7"); "" }
  def h = { calltracer.trace("h", "fix/CallTracer.scala", "24:7"); "" }

}
