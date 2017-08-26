import java.io.BufferedReader
import java.io.InputStream
import java.io.File
import java.util.stream.Collectors
import java.util.stream.*

fun load_csv(filename:String) : MutableList<Map<String,Any>> {
	val inputStream = File(filename).inputStream()
	val lines = mutableListOf<String>()
	inputStream.bufferedReader().useLines { xs -> xs.forEach { lines.add(it)} }
  val iter = lines.iterator()
  val keys = iter.next().split(",")
  println(keys)

  val df = mutableListOf<Map<String,Any>>()
  iter
    .forEach { line ->
      val vals = line.split(",")
      val obj = keys.zip(vals).toMap()
      println(obj)
      df.add( obj )
  }
  return df
}

fun main(args : Array<String>) {
  val df = load_csv("mini.csv")
}
