import java.io.File
fun main(args : Array<String> ) { 
  val links = File("./spark/dummpy.txt").readLines().map { 
    val (a, b) = it.split(" ")
    Pair(a, b)
  }.groupBy { 
    it.first
  }.toList().map { 
    it
  }
  
  var ranks = links.map { 
    val link = it.first
    Pair(link, 1.0)
  }.toMap()

  for( i in (0..1000) ){
    val ra = links.map {
      val (link, urls) = it
      val neigbs = urls.map { it.second }
      val rank = ranks[link]!!

      // contributeを計算
      val len = neigbs.size
      val recal = neigbs.map { 
        Pair(it, rank.toDouble()/len)
      }
      recal
    }.toList()
    
    // update ranks
    ranks = ra.flatten().groupBy {
      it.first
    }.toList()
    .map { 
      val nextRank = it.second.map { it.second }.reduce { y,x -> y+x } * 0.85 + 0.15
      val link = it.first
      Pair(link, nextRank)
    }.toMap()
  }
  ranks.toList().map { 
    val link = it.first
    val rank = it.second
    println("$link $rank")
  }

}
