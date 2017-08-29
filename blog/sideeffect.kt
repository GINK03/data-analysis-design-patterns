
fun main(args : Array<String>) {
  val m = mutableMapOf<Int, MutableList<Int>>()

  (0..100000).map { 
    when { 
      it%2 == 0 -> { if( m.get(0) == null ) { m[0] = mutableListOf() }; m[0]!!.add(it) }
      else -> { if( m.get(1) == null ) { m[1] = mutableListOf() }; m[1]!!.add(it) } 
    }
  }
  m.map { kv ->
    val (k, v) = kv
    println("$k $v")
  }
}
