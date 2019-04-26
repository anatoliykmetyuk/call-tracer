package fix

object CallTracer {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.trace("foo at fix/CallTracer.scala:13:7"); stuff }
  def g(x: Int, y: Double) = { calltracer.trace("g at fix/CallTracer.scala:14:7"); {
    println(s"This is a method with a body")
    x
  } }

  def f() = { calltracer.trace("f at fix/CallTracer.scala:19:7"); "" }
  def h = { calltracer.trace("h at fix/CallTracer.scala:20:7"); "" }

}
