package fix

object CallTracerVisit {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = { calltracer.visit(bar, stuff, x); stuff }
  def g(x: Int, y: Double) = { calltracer.visit(x); {
    println(s"This is a method with a body")
    x
  } }

}
