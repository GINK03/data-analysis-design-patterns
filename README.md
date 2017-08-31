# Kotlin, Rubyによるビッグデータに耐えうるデータ分析のデザイン

明確に言葉にならなかった部分がだんだんボキャブラリが増えたり、知っていることが増えたりしたので説明できると思います  

データ分析という言葉が出てくる話であると、ほとんどの場合、Excel, Python, R, SQLなどで分析する環境がほとんどです  
一部、BigDataの話ではJavaなどがあるので、JVMで動く言語Scalaなどを用いることができます

競技プログラミングでしばらくKotlinを触っていた関係もあり、以外とKotlinなどによるデータ分析も可能で便利なのではないかという印象を持ちました  

## SQLとよく対比されるデータ構造の後ろに書くラムダ式
LinqはMicrosoft社のC#に搭載されている機能で、ArrayやListの後ろにラムダ式を記述することで、任意のデータ変換とオペレーションをできるようにしたものです　　

これはKotlinやRubyでも実行可能です(別にLinqという名称ではないでしょうが)　　　

例えば、ある数字のリストに対して、2で割り切れるものを残して、二乗するプログラムはこのようになります  
```ruby
xs = (0..20).map { |x| x }
# 2で割り切れる
arr = xs.select { |x| x%2 == 0 }.map { |x| x**2 }
p arr
```
```console
$ ruby example.rb 
[0, 4, 16, 36, 64, 100, 144, 196, 256, 324, 400]
```
```kotlin
fun main(args : Array<String>) {
  val df = (0..20).map { it }
  val arr = df.filter { it%2　== 0 }.map { it*it }
  println(arr)
}
```
```console
$ kotlin example.jar 
[0, 4, 16, 36, 64, 100, 144, 196, 256, 324, 400]
```
このように、whereで条件で切り出して、結果を二乗するような状態を作る結果が得られます　　

例えば、pandasで同じようなするとこのようになります  
```python
from pandas import DataFrame
raw = [i for i in range(20)]
df = DataFrame( { '1' : raw } )
df = df[lambda x:x['1'] % 2 == 0 ]
df = df.apply(lambda x:x**2)
print( df.head(20).values.tolist() )
```
## これは（GCP DataFlow, Apache Beamの）パイプラインのanalogyでもある
Google Computing Cloudで利用されているDataFlow(OSSバージョンの名前はApache Beam)というサービスがあります  

DataFlowは、関数をPipelineを連続して繋げていくことでデータの変形、集計を行います  

例えば、DataFlowの主要な実行コードはこのようになっており、ほぼ、KotlinとRubyのメソッドチェーンに対応していることがわかると思います
```java
Pipeline p = Pipeline.create(options);
p.apply( TextIO.Read.from("gs://dataflow-samples/shakespeare/*") ) // 入力バケット
 .apply( ParDo.named("ExtractWords1").of( new 処理1() ) )           // 処理1 
 .apply( ParDo.named("make kv").of( new 処理2() ) )                 // 処理2
 .apply( GroupByKey.<String, String>create() )                     // キーでGroupByする
 .apply( ParDo.named("FormatResults").of( new 処理3() ) )           // 処理3
 .apply( TextIO.Write.to(OutputGS));
// run this pipeline 
p.run();
```
## CSVファイルをRubyでデータをPipeline形式で処理する
CSVファイルをRubyのPipeline形式で処理するとこのようになります　　

Rubyはこのように、Pandasなどのフレームワークに相当する[daru](https://github.com/SciRuby/daru)を使わずとも処理することができます  

この例では、車の統計の情報で18MBytesのデータセットです　　
```ruby
# 各メーカで燃費が良い車種トップ5 
df.map{ |x| 
  d = OpenStruct.new
  d.make = x['make'] 
  d.model = x['model'] 
  d.cost = x['fuelCost08']
  d
}.group_by { |x| 
  x.make
}.map { |xs|
  make, arr = xs
  arr.map { |x| 
    d = OpenStruct.new
    d.model = x.model
    d.cost = x.cost.to_i
    d
  }.to_set.to_a.sort_by { |x| 
    x.cost * -1
  }.slice(0..20).map { |x| 
    print x.model, ' ', x.cost, "\n"
  }
}
```
## CSVファイルをKotlinでデータをPipeline形式で集計する
Kotlinでも同様の記述ができて、Pipeline形式のような表現することができます  

ここでは記していませんが、[letを用いることで、データや関数の戻り値を次の関数につなぐことも](https://discuss.kotlinlang.org/t/pipe-forward-operator/2098/14)できます　　
```kotlin
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
```
## メモリを使用するオペレーション
（副作用という関数型言語の言葉を使おうかと、悩みましたが、微妙に副作用がグローバル変数による関数の参照透過性が消失することのみをさす文脈があり、メモリを使用するという言葉に変えました。）  

一時的に状態を保存すること期待して、特定のキーで集約してgroupbyのような構造を取る時、何かをkeyにソートするときなどは、メモリにデータを貯める必要があります 　

メモリにデータを貯める処理を入れるということは、実質有限なメモリというリソースを消耗して、一時的にオンメモリにする必要があり、これができるかできないかで大きく扱いは変わってきます  

HadoopなどのMap ReduceなどではReduceの台数を増やしたり、調整したりすることでこれらを達成していましたが、一台のマシンでやる時には常にメモリが溢れないことを考慮しながら、分析戦略を立てる必要があります。　　

メモリがマシンに収まらない時は、GCPやAWSでDataFlowやElasticMapReduceなどの必要なサービスを選択して、クラウドで計算するのも十分考慮すべき選択肢です　　

例えば、特定のキーで集計する必要があるGroupByは実行時間中、メモリに前に関数が呼び出された時の処理の内容を記録している必要があります　　
<p align="center">
  <img width="350px" src="https://user-images.githubusercontent.com/4949982/29836324-da97cc48-8d2f-11e7-8ded-d3ecc5c5e606.png">
</p>

例えば、次のようなKotlinのコードは、変数mに状態を記録しているのですが、メモリ4GByteのマシンでは動作させることができませんでした
```kotlin
fun main(args : Array<String>) {
  val m = mutableMapOf<Int, MutableList<Int>>()
  (0..Int.MAX_VALUE).map { 
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
```

## Apache SparkのExample  
Apache Sparkプロジェクトの同様の問題を解かせることで、機能的に同等であることを示します
### Pi Estimation
Kotlinではこのようになります  
KotlinとRubyではKotlinの方が早かったです　　
```kotlin:kotlin
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
```
Rubyではこのようになります  
```ruby
rand = Random.new(1234)
MAX = 10000000
size = (0..MAX).map { |x| 
  a,b = rand.rand, rand.rand
  a**2 + b**2
}.select { |x| 
  x < 1.0
}.size
print 'estimate pi ', 4*size.to_f/MAX, "\n"
```
### Word Count
ビッグデータでよくあるある例である単語の数え上げです　　

Kotlinによる実装です
```kotlin
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
```
Rubyによる実装です
```ruby
File.readlines('911report.txt').map { |x|
  x.split(' ').map { |x| x }
}.flatten.group_by { |x| 
  x 
}.to_a.map { |x| 
  key, arr = x
  [key, arr.size] 
}.sort_by { |x|
  x[1]*-1
}.slice(0..20).map { |x| 
  p x 
}
```

## SQLとラムダ式での等価な例
機能的に等価ですが、SQLとKotlinでどう違うのか、記していきます  
使用したデータは[こちら](http://www.fueleconomy.gov/feg/ws/index.shtml)　　
### Distinct
車のメーカでDistinctします
```sql
SELECT distinct make FROM vehicles;
```
```kotlin
// メーカ名をdistinctする場合
df.map {
  val maker = it["make"]
  maker
}.toSet().map {
  println(it)
}
```
### sort and limit
燃費が悪いmakerのモデルを表示
```sql
SELECT make,model,fuel FROM vehicles ORDER BY fuel DESC LIMIT 20;
```
```kotlin
//燃費が悪いメーカのモデルを降順で表示
df.map {
  val maker = it["make"]
  val model = it["model"]
  val cost  = it["fuelCost08"].toString().toInt()
  Triple(maker, model, cost)
}.sortedBy {
  it.third * -1
}.slice(0..20).map {
  println(it)
}
```
### like
モデルの名前に”A”が含まれているもんを取り出す
```sql
SELECT model FROM vehicles WHERE model like '%A%';
```
```kotlin
//モデル名に'A'という文字列が入るものを取り出す
df.map {
  val model = it["model"].toString()
  model
}.filter {
  it.contains("A")
}.map {
  println(it)
}
```
### order by
燃費の合計が10,000を超えるメーカーを取り出す
```sql
SELECT make, Sum(cost)
FROM vehicles
GROUP BY make
HAVING Sum(cost) >= 10000;
```
```kotlin
// 燃費の合算が10000以上のmakerを取り出す
df.map {
  val maker = it["make"]
  val cost  = it["fuelCost08"].toString().toInt()
  Pair(maker, cost)
}.groupBy {
  it.first
}.toList().map { 
  val (maker, vals) = it
  val sum = vals.map { it.second }.reduce { y,x -> y+x }
  Pair(maker, sum)
}.filter {
  it.second >= 10000
}.map {
  println(it)
}
```

## 圏論の観点から
ラムダ式によるデータの変換は、圏論の話題として時たま上がります　　

圏論ではこう言ったデータを変換させるラムダ式を射(関数)として、見做すことができます 　

私自身、すごく理解しているわけでないですが、関数の合成や変換や可逆性、データの破壊されうるポイントなどを狭い関数の粒度で理解していると、潜在的なリスクがどこに潜んでいるのか、メモリが大量に必要で分析プロセスがうまく回らない点はどこなのかという感覚値を持つことができて、泥沼にハマることを未然に防ぐことができます  


<p align="center">
  <img width="　550px" src="https://user-images.githubusercontent.com/4949982/29902345-8ae9285e-8e38-11e7-8b86-e7805b6a3233.png">
</p>
<div align="center"> 図1.  </div>

### 双対性　　

圏論ではデータは双対性を持つことが、期待されることが多いですが、全てのデータに関してそれが成立するわけでありません　　

元のデータが復元できなくなるような、fold(reduce)などの畳み込み操作の一部は、データの情報量が落ち、復元不能になります　　

このような操作が入る時には、より慎重に、分析関数を設計する必要があります

## まとめ
ラムダ式によるデータ分析の視点が、SQLとほぼ同列に扱えて、理論的にも数学的な背景を持つので、何に着目すべきか、リソースの枯渇の心配があるポイントはどこなのか、スケールアウトできるシステムであるDataFlow(Apache Beam)との類似性などを示しました  

ビッグデータの分析は一応の体系にできそうな印象を受けました  



