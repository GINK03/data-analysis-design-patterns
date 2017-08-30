import java.lang.Math
import java.util.stream.Collectors

fun parallel() {
  val MAX = 10000000
  val size = (0..MAX).toList().stream().map { 
    val a = Math.random() 
    val b = Math.random() 
    a*a + b*b
  }.filter { 
    it < 1.0
  }.collect(Collectors.toList()).size
  println("parallel Pi estimated, ${4.0 * size.toDouble()/MAX} ")
}

fun default() {
  val MAX = 10000000
  val size = (0..MAX).map { 
    val a = Math.random() 
    val b = Math.random() 
    a*a + b*b
  }.filter { 
    it < 1.0
  }.size
  println("default Pi estimated, ${4.0 * size.toDouble()/MAX} ")
}
fun main( args : Array<String> ) {
  var startTime = `System`.currentTimeMillis()
  parallel()
  println("parallel elapsed, ${`System`.currentTimeMillis() - startTime} millsec")
  startTime = `System`.currentTimeMillis()
  default()
  println("default elapsed, ${`System`.currentTimeMillis() - startTime} millsec")
}
