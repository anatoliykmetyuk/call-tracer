/*
rule = CallTracer
CallTracer = {
  mode = visit
  targets = [foo, g]
  excludedArgs = [y]
  excludedArgsFor.foo = [char]
}
*/
package fix

object CallTracerVisit {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = stuff
  def g(x: Int, y: Double) = {
    println(s"This is a method with a body")
    x
  }

}
