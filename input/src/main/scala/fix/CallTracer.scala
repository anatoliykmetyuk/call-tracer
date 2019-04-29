/*
rule = CallTracer
CallTracer = {
  outputPath = false
  argRules = [
    {
      forType = ["Int"]
    , action  = "x.toString"
    }
  ]
}
*/
package fix

object CallTracer {

  def foo[T](bar: T, char: Int)(stuff: String)(implicit x: Double, y: Double): String = stuff
  def g(x: Int, y: Double) = {
    println(s"This is a method with a body")
    x
  }

  def f() = ""
  def h = ""

}
