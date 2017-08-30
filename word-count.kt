import  java.io.File
fun main(args : Array<String>) {
  File("911report.txt").readLines().map { 
    it.split(" ")
  }.flatten().groupBy { 
    it
  }.toList().map {  kv ->
    val (word, arr) = kv
    Pair(word, arr.size)
  }.sortedBy { 
    it.second * -1
  }.slice(0..20).map { 
    println( "${it.first} ${it.second}" )
  }
}  

