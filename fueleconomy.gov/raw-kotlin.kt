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

  val df = mutableListOf<Map<String,Any>>()
  iter
    .forEach { line ->
      val vals = line.split(",")
      val obj = keys.zip(vals).toMap()
      df.add( obj )
  }
  return df
}

fun main(args : Array<String>) {
  val df = load_csv("./vehicles.csv")

  // make作成したメーカの数でソート
  val makers = df.map { 
    Pair(it["make"], 1)
  }.groupBy{ kv ->
    kv.first
  }.map { kv ->
    val (k,v) = kv
    Pair(k, v.size )
  }.sortedBy { 
    it.second*-1
  }
  println(makers)

  // 車の平均の燃費（/年）
  val nenpi = df.map { 
    Triple(it["make"], it["fuelCost08"], it["fuelCostA08"])
  }.groupBy{ kv ->
    kv.first
  }.toList().map { kv ->
    val name = kv.first
    val tris = kv.second
    val mean = tris.map { it.second.toString().toDouble() }.reduce { y,x -> y+x } / tris.size
    Pair(name, mean)
  }.sortedBy { 
    it.second
  }.filter {
    it.second > 0.0
  }
  println(nenpi)
  
  // 各メーカで燃費が良い車種トップ5
  df.map { 
    Triple( it["make"], it["model"], it["fuelCost08"] )
  }.filter {
    it.third.toString().toDouble() > 0.0
  }.groupBy { 
    it.first
  }.toList().map {
    val maker = it.first
    val arrs  = it.second.map { 
      val model = it.second
      val cost  = it.third.toString().toDouble()
      Pair(model, cost)
    }.toSet().toList().sortedBy { 
      it.second*-1
    }
    val sliced = when { arrs.size >= 3 -> arrs.slice(0..2);  else -> arrs }
    println("$maker $sliced")
  }
}
