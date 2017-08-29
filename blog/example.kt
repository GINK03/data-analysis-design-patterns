
fun main(args : Array<String>) {
  val df = (0..20).map { it }
  val arr = df.filter { it%2== 0 }.map { it*it }
  println(arr)
}
