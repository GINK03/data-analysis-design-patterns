
## SQL, pandas,  関数型におけるそれぞれの集計・分析方法の互換性

### 乱立するデータ集計技術
ビッグデータだの、人工知能だのバズワードが様々に叫ばれていますが、今でも主流はエクセルで分析しているということを聞いたりします。  
エクセルを超えた量のデータを扱う時には綺麗に成型されている時に限り、SQLなどが使えますが、データが汚かったり、膨大な量になってくると関数型言語からヒントを得たMapReduce系の集計フレームワークが使われます。  

また、さすがにVBScriptでデータ集計したくないという人もそれなりにいて、Excelのデータをpandasに投入して必要なデータを集計している人もいます。  

昔からデータを貯める、分析するということを生業にしていた人は、ビッグデータの考えをそのままSQLを適応して、SQLで集計したりしています。　　

色々な集計方法が出てきた今、改めて、主に私の周りで使われている集計技術(アドテクの一部の領域)について、それぞれが確かに互換性能があることを示したいと思います。  


### 今回評価する方法
以下の三つを評価します
#### SQL
最も昔からあるデータ構造の管理の形だと思います。  
私も学生時代はPHP + MySQL + Perl(or Ruby)でのWebシステムを作ったことがあります。  
メリットとしてはSQLは使える人が多く、システム屋さんも知識的に保有していることが多いので、人の流動性が確保しやすいという印象があります。  
デメリットとしては、データが正規化されている必要があるので、実験的なデータを取り込むのが弱く、アドホックでスピード感がある実験を繰り返せる環境にはあまり向いていないです（実体験ベース）

#### Pandas
直近でも経験したのですが、Excelの膨大なデータを渡されて分析してと投げられることがあります。  
私は、ExcelをCSVにダンプしてPythonやRubyに投入して分析することしていたのですが、当時同じメンバーがExcel VB Macroで分析するということをやっており、[Pythonでの機械学習のアウトプット]-> [Excel]の繋ぎに不便を感じていました  
今年ぐらいから、周りのデータサイエンス業界と、Kaggleの強いツール（Kaggleで多く使われているツールはよく流行るので参考になります）という角度で、Excelでデータをもらっても、基本的に、PythonのPandasで処理することになりました。  
メリットとしては、Pythonはそれなりに使える人が多いし、Excelをこねくり回すよりだいぶマシで、SQLとやりたいことは同じことができます  
デメリットとしては、Pandasの作り自体が独自のエコシステムを形成しており、SQLや関数型の考えとも微妙に異なっており、独自のポジジョンにいることで学習コストがかさみがちです

#### 関数型ライク
MapReduceのアーキテクチャが提案されてから、どんなに巨大なデータでも、この仕組みの上に載せることで、集計が可能になりました。  
関数型言語のmap関数とreduce関数にヒントを得て作られており、畳み込み（fold）に該当する操作を行うReduceのリソースをうまく分散処理させられれば、どんなデータでも処理可能です。　　 

メリットとしては、プログラミングをして集計をするので、かなり柔軟であり、複雑怪奇な非構造化データでも処理可能です。  
デメリットとしては、プログラムをかけないレベルの人は集計するのも困難になることが挙げられます。  

#### 互換性を示す
圏論などを用いて、数式上で、データの操作が等価であることを示してもいいのですが、今回は、dataquest.ioさんが一般的にSQLを用いて基礎のデータサイエンティストが集計できるべき角度のデータとそのクエリをその教育プランで行なっており、それを他のデータ集計方法でも行えることを示すことで、基礎的な側面においては、等価であることを示したいと考えています  
（よろしければ、[dataquest.io](https://www.dataquest.io/dashboard)さんのコンテンツも見てみてください。基礎的な側面においては、充実している印象があります。）  

### 使用するデータ

#### jobs.db
sqlite形式で保存したデータベースです。そんなに大きいデータでないので、これを今回、集計してきます  
大学の専攻で、就職率、就職した先、人数、女性の比率などが記されています。
```console
sqlite> PRAGMA table_info(recent_grads);
0|index|INTEGER|0||0
1|Rank|INTEGER|0||0
2|Major_code|INTEGER|0||0
3|Major|TEXT|0||0
4|Major_category|TEXT|0||0
5|Total|INTEGER|0||0
6|Sample_size|INTEGER|0||0
7|Men|INTEGER|0||0
8|Women|INTEGER|0||0
9|ShareWomen|REAL|0||0
10|Employed|INTEGER|0||0
11|Full_time|INTEGER|0||0
12|Part_time|INTEGER|0||0
13|Full_time_year_round|INTEGER|0||0
14|Unemployed|INTEGER|0||0
15|Unemployment_rate|REAL|0||0
16|Median|INTEGER|0||0
17|P25th|INTEGER|0||0
18|P75th|INTEGER|0||0
19|College_jobs|INTEGER|0||0
20|Non_college_jobs|INTEGER|0||0
21|Low_wage_jobs|INTEGER|0||0
```
#### factbook.db
sqlite形式で保存したデータベースです  
主に、各国の人口や出生率など、国力を表すKPIが多く記されています  
テーブルの構造はこのようになっています  
```console
sqlite> PRAGMA table_info(facts);
0|id|INTEGER|1||1
1|code|varchar(255)|1||0
2|name|varchar(255)|1||0
3|area|integer|0||0
4|area_land|integer|0||0
5|area_water|integer|0||0
6|population|integer|0||0
7|population_growth|float|0||0
8|birth_rate|float|0||0
9|death_rate|float|0||0
10|migration_rate|float|0||0
11|created_at|datetime|0||0
12|updated_at|datetime|0||0
```

データが少ないので、[github](https://github.com/GINK03/data-analysis-design-patterns)で管理しています。

### jupyter notebook上での実行です
一つのSQLのクエリとして知っておくべき最小での粒度での、例をSQL, Pandas, 関数型の順で示します  
本当は関数型はKotlinで書きたかったのですが、Jupyter上でPythonとKotlinを両方一つのノートブックで使う方法がわからなかったので、Rubyで書いています(Rubyは別に関数型言語ではないですが、SyntaxはGoogle Cloud DataFlowやSparkなどに似せられるので、そのように書きました。)


```python
# SQLITEのデータをメモリ上にロードします
%load_ext sql
%sql sqlite:////var/jobs.db

# jobs.dbのデータ構造はこのようになっています
%sql SELECT * FROM recent_grads LIMIT 3;
```

    The sql extension is already loaded. To reload it, use:
      %reload_ext sql
    
    .





<table>
    <tr>
        <th>index</th>
        <th>Rank</th>
        <th>Major_code</th>
        <th>Major</th>
        <th>Major_category</th>
        <th>Total</th>
        <th>Sample_size</th>
        <th>Men</th>
        <th>Women</th>
        <th>ShareWomen</th>
        <th>Employed</th>
        <th>Full_time</th>
        <th>Part_time</th>
        <th>Full_time_year_round</th>
        <th>Unemployed</th>
        <th>Unemployment_rate</th>
        <th>Median</th>
        <th>P25th</th>
        <th>P75th</th>
        <th>College_jobs</th>
        <th>Non_college_jobs</th>
        <th>Low_wage_jobs</th>
    </tr>
    <tr>
        <td>0</td>
        <td>1</td>
        <td>2419</td>
        <td>PETROLEUM ENGINEERING</td>
        <td>Engineering</td>
        <td>2339</td>
        <td>36</td>
        <td>2057</td>
        <td>282</td>
        <td>0.120564344</td>
        <td>1976</td>
        <td>1849</td>
        <td>270</td>
        <td>1207</td>
        <td>37</td>
        <td>0.018380527</td>
        <td>110000</td>
        <td>95000</td>
        <td>125000</td>
        <td>1534</td>
        <td>364</td>
        <td>193</td>
    </tr>
    <tr>
        <td>1</td>
        <td>2</td>
        <td>2416</td>
        <td>MINING AND MINERAL ENGINEERING</td>
        <td>Engineering</td>
        <td>756</td>
        <td>7</td>
        <td>679</td>
        <td>77</td>
        <td>0.10185185199999999</td>
        <td>640</td>
        <td>556</td>
        <td>170</td>
        <td>388</td>
        <td>85</td>
        <td>0.117241379</td>
        <td>75000</td>
        <td>55000</td>
        <td>90000</td>
        <td>350</td>
        <td>257</td>
        <td>50</td>
    </tr>
    <tr>
        <td>2</td>
        <td>3</td>
        <td>2415</td>
        <td>METALLURGICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>856</td>
        <td>3</td>
        <td>725</td>
        <td>131</td>
        <td>0.153037383</td>
        <td>648</td>
        <td>558</td>
        <td>133</td>
        <td>340</td>
        <td>16</td>
        <td>0.024096386</td>
        <td>73000</td>
        <td>50000</td>
        <td>105000</td>
        <td>456</td>
        <td>176</td>
        <td>0</td>
    </tr>
</table>



### 専攻(Major)列に限定して、10行取り出す


```python
# sql
%sql SELECT Rank, Major FROM recent_grads LIMIT 10;
```

<table>
    <tr>
        <th>Rank</th>
        <th>Major</th>
    </tr>
    <tr>
        <td>1</td>
        <td>PETROLEUM ENGINEERING</td>
    </tr>
    <tr>
        <td>2</td>
        <td>MINING AND MINERAL ENGINEERING</td>
    </tr>
    <tr>
        <td>3</td>
        <td>METALLURGICAL ENGINEERING</td>
    </tr>
    <tr>
        <td>4</td>
        <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
    </tr>
    <tr>
        <td>5</td>
        <td>CHEMICAL ENGINEERING</td>
    </tr>
    <tr>
        <td>6</td>
        <td>NUCLEAR ENGINEERING</td>
    </tr>
    <tr>
        <td>7</td>
        <td>ACTUARIAL SCIENCE</td>
    </tr>
    <tr>
        <td>8</td>
        <td>ASTRONOMY AND ASTROPHYSICS</td>
    </tr>
    <tr>
        <td>9</td>
        <td>MECHANICAL ENGINEERING</td>
    </tr>
    <tr>
        <td>10</td>
        <td>ELECTRICAL ENGINEERING</td>
    </tr>
</table>




```python
# pandasで処理するために、まずdataframe(df)に読み込みます
import sqlite3
import pandas as pd
conn = sqlite3.connect("/var/jobs.db")
df = pd.read_sql_query("SELECT * FROM recent_grads ;", conn)
```


```python
rank_major = df[["Rank", "Major"]]
rank_major.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Rank</th>
      <th>Major</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>1</td>
      <td>PETROLEUM ENGINEERING</td>
    </tr>
    <tr>
      <th>1</th>
      <td>2</td>
      <td>MINING AND MINERAL ENGINEERING</td>
    </tr>
    <tr>
      <th>2</th>
      <td>3</td>
      <td>METALLURGICAL ENGINEERING</td>
    </tr>
    <tr>
      <th>3</th>
      <td>4</td>
      <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
    </tr>
    <tr>
      <th>4</th>
      <td>5</td>
      <td>CHEMICAL ENGINEERING</td>
    </tr>
    <tr>
      <th>5</th>
      <td>6</td>
      <td>NUCLEAR ENGINEERING</td>
    </tr>
    <tr>
      <th>6</th>
      <td>7</td>
      <td>ACTUARIAL SCIENCE</td>
    </tr>
    <tr>
      <th>7</th>
      <td>8</td>
      <td>ASTRONOMY AND ASTROPHYSICS</td>
    </tr>
    <tr>
      <th>8</th>
      <td>9</td>
      <td>MECHANICAL ENGINEERING</td>
    </tr>
    <tr>
      <th>9</th>
      <td>10</td>
      <td>ELECTRICAL ENGINEERING</td>
    </tr>
  </tbody>
</table>
</div>

```ruby
%%ruby
## Jupyter notebookでrubyで記述するとこのように表現できる（毎回読み込む必要がある）
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}&.map { |xs| 
    [xs["Rank"], xs["Major"]]
}&.slice(0..9).map { |xs| 
    p xs
}
```

    [1, "PETROLEUM ENGINEERING"]
    [2, "MINING AND MINERAL ENGINEERING"]
    [3, "METALLURGICAL ENGINEERING"]
    [4, "NAVAL ARCHITECTURE AND MARINE ENGINEERING"]
    [5, "CHEMICAL ENGINEERING"]
    [6, "NUCLEAR ENGINEERING"]
    [7, "ACTUARIAL SCIENCE"]
    [8, "ASTRONOMY AND ASTROPHYSICS"]
    [9, "MECHANICAL ENGINEERING"]
    [10, "ELECTRICAL ENGINEERING"]


### Rank,Major_code,Major,Major_category,Total列に限定して、20件の行を取り出す

```python
%sql SELECT Rank,Major_code,Major,Major_category,Total FROM recent_grads limit 20;
```

<table>
    <tr>
        <th>Rank</th>
        <th>Major_code</th>
        <th>Major</th>
        <th>Major_category</th>
        <th>Total</th>
    </tr>
    <tr>
        <td>1</td>
        <td>2419</td>
        <td>PETROLEUM ENGINEERING</td>
        <td>Engineering</td>
        <td>2339</td>
    </tr>
    <tr>
        <td>2</td>
        <td>2416</td>
        <td>MINING AND MINERAL ENGINEERING</td>
        <td>Engineering</td>
        <td>756</td>
    </tr>
    <tr>
        <td>3</td>
        <td>2415</td>
        <td>METALLURGICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>856</td>
    </tr>
    <tr>
        <td>4</td>
        <td>2417</td>
        <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
        <td>Engineering</td>
        <td>1258</td>
    </tr>
    <tr>
        <td>5</td>
        <td>2405</td>
        <td>CHEMICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>32260</td>
    </tr>
    <tr>
        <td>6</td>
        <td>2418</td>
        <td>NUCLEAR ENGINEERING</td>
        <td>Engineering</td>
        <td>2573</td>
    </tr>
    <tr>
        <td>7</td>
        <td>6202</td>
        <td>ACTUARIAL SCIENCE</td>
        <td>Business</td>
        <td>3777</td>
    </tr>
    <tr>
        <td>8</td>
        <td>5001</td>
        <td>ASTRONOMY AND ASTROPHYSICS</td>
        <td>Physical Sciences</td>
        <td>1792</td>
    </tr>
    <tr>
        <td>9</td>
        <td>2414</td>
        <td>MECHANICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>91227</td>
    </tr>
    <tr>
        <td>10</td>
        <td>2408</td>
        <td>ELECTRICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>81527</td>
    </tr>
    <tr>
        <td>11</td>
        <td>2407</td>
        <td>COMPUTER ENGINEERING</td>
        <td>Engineering</td>
        <td>41542</td>
    </tr>
    <tr>
        <td>12</td>
        <td>2401</td>
        <td>AEROSPACE ENGINEERING</td>
        <td>Engineering</td>
        <td>15058</td>
    </tr>
    <tr>
        <td>13</td>
        <td>2404</td>
        <td>BIOMEDICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>14955</td>
    </tr>
    <tr>
        <td>14</td>
        <td>5008</td>
        <td>MATERIALS SCIENCE</td>
        <td>Engineering</td>
        <td>4279</td>
    </tr>
    <tr>
        <td>15</td>
        <td>2409</td>
        <td>ENGINEERING MECHANICS PHYSICS AND SCIENCE</td>
        <td>Engineering</td>
        <td>4321</td>
    </tr>
    <tr>
        <td>16</td>
        <td>2402</td>
        <td>BIOLOGICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>8925</td>
    </tr>
    <tr>
        <td>17</td>
        <td>2412</td>
        <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
        <td>Engineering</td>
        <td>18968</td>
    </tr>
    <tr>
        <td>18</td>
        <td>2400</td>
        <td>GENERAL ENGINEERING</td>
        <td>Engineering</td>
        <td>61152</td>
    </tr>
    <tr>
        <td>19</td>
        <td>2403</td>
        <td>ARCHITECTURAL ENGINEERING</td>
        <td>Engineering</td>
        <td>2825</td>
    </tr>
    <tr>
        <td>20</td>
        <td>3201</td>
        <td>COURT REPORTING</td>
        <td>Law &amp; Public Policy</td>
        <td>1148</td>
    </tr>
</table>


```python
step2 = df[["Rank","Major_code","Major","Major_category","Total"]]
step2.head(20) 
```

<div>
<style>
    .dataframe thead tr:only-child th {
        text-align: right;
    }

    .dataframe thead th {
        text-align: left;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Rank</th>
      <th>Major_code</th>
      <th>Major</th>
      <th>Major_category</th>
      <th>Total</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>1</td>
      <td>2419</td>
      <td>PETROLEUM ENGINEERING</td>
      <td>Engineering</td>
      <td>2339</td>
    </tr>
    <tr>
      <th>1</th>
      <td>2</td>
      <td>2416</td>
      <td>MINING AND MINERAL ENGINEERING</td>
      <td>Engineering</td>
      <td>756</td>
    </tr>
    <tr>
      <th>2</th>
      <td>3</td>
      <td>2415</td>
      <td>METALLURGICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>856</td>
    </tr>
    <tr>
      <th>3</th>
      <td>4</td>
      <td>2417</td>
      <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
      <td>Engineering</td>
      <td>1258</td>
    </tr>
    <tr>
      <th>4</th>
      <td>5</td>
      <td>2405</td>
      <td>CHEMICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>32260</td>
    </tr>
    <tr>
      <th>5</th>
      <td>6</td>
      <td>2418</td>
      <td>NUCLEAR ENGINEERING</td>
      <td>Engineering</td>
      <td>2573</td>
    </tr>
    <tr>
      <th>6</th>
      <td>7</td>
      <td>6202</td>
      <td>ACTUARIAL SCIENCE</td>
      <td>Business</td>
      <td>3777</td>
    </tr>
    <tr>
      <th>7</th>
      <td>8</td>
      <td>5001</td>
      <td>ASTRONOMY AND ASTROPHYSICS</td>
      <td>Physical Sciences</td>
      <td>1792</td>
    </tr>
    <tr>
      <th>8</th>
      <td>9</td>
      <td>2414</td>
      <td>MECHANICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>91227</td>
    </tr>
    <tr>
      <th>9</th>
      <td>10</td>
      <td>2408</td>
      <td>ELECTRICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>81527</td>
    </tr>
    <tr>
      <th>10</th>
      <td>11</td>
      <td>2407</td>
      <td>COMPUTER ENGINEERING</td>
      <td>Engineering</td>
      <td>41542</td>
    </tr>
    <tr>
      <th>11</th>
      <td>12</td>
      <td>2401</td>
      <td>AEROSPACE ENGINEERING</td>
      <td>Engineering</td>
      <td>15058</td>
    </tr>
    <tr>
      <th>12</th>
      <td>13</td>
      <td>2404</td>
      <td>BIOMEDICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>14955</td>
    </tr>
    <tr>
      <th>13</th>
      <td>14</td>
      <td>5008</td>
      <td>MATERIALS SCIENCE</td>
      <td>Engineering</td>
      <td>4279</td>
    </tr>
    <tr>
      <th>14</th>
      <td>15</td>
      <td>2409</td>
      <td>ENGINEERING MECHANICS PHYSICS AND SCIENCE</td>
      <td>Engineering</td>
      <td>4321</td>
    </tr>
    <tr>
      <th>15</th>
      <td>16</td>
      <td>2402</td>
      <td>BIOLOGICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>8925</td>
    </tr>
    <tr>
      <th>16</th>
      <td>17</td>
      <td>2412</td>
      <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
      <td>Engineering</td>
      <td>18968</td>
    </tr>
    <tr>
      <th>17</th>
      <td>18</td>
      <td>2400</td>
      <td>GENERAL ENGINEERING</td>
      <td>Engineering</td>
      <td>61152</td>
    </tr>
    <tr>
      <th>18</th>
      <td>19</td>
      <td>2403</td>
      <td>ARCHITECTURAL ENGINEERING</td>
      <td>Engineering</td>
      <td>2825</td>
    </tr>
    <tr>
      <th>19</th>
      <td>20</td>
      <td>3201</td>
      <td>COURT REPORTING</td>
      <td>Law &amp; Public Policy</td>
      <td>1148</td>
    </tr>
  </tbody>
</table>
</div>

### 女性率が0.5(50%)を超える専攻と女性率の20件の行を表示する

```python
#sql
%sql SELECT Major,ShareWomen FROM recent_grads WHERE ShareWomen>0.5 limit 20;
```

<table>
    <tr>
        <th>Major</th>
        <th>ShareWomen</th>
    </tr>
    <tr>
        <td>ACTUARIAL SCIENCE</td>
        <td>0.535714286</td>
    </tr>
    <tr>
        <td>COMPUTER SCIENCE</td>
        <td>0.578766338</td>
    </tr>
    <tr>
        <td>ENVIRONMENTAL ENGINEERING</td>
        <td>0.558548009</td>
    </tr>
    <tr>
        <td>NURSING</td>
        <td>0.896018988</td>
    </tr>
    <tr>
        <td>INDUSTRIAL PRODUCTION TECHNOLOGIES</td>
        <td>0.75047259</td>
    </tr>
    <tr>
        <td>COMPUTER AND INFORMATION SYSTEMS</td>
        <td>0.7077185020000001</td>
    </tr>
    <tr>
        <td>INFORMATION SCIENCES</td>
        <td>0.526475764</td>
    </tr>
    <tr>
        <td>APPLIED MATHEMATICS</td>
        <td>0.75392736</td>
    </tr>
    <tr>
        <td>PHARMACOLOGY</td>
        <td>0.524152583</td>
    </tr>
    <tr>
        <td>OCEANOGRAPHY</td>
        <td>0.688999173</td>
    </tr>
    <tr>
        <td>MATHEMATICS AND COMPUTER SCIENCE</td>
        <td>0.927807246</td>
    </tr>
    <tr>
        <td>COGNITIVE SCIENCE AND BIOPSYCHOLOGY</td>
        <td>0.854523227</td>
    </tr>
    <tr>
        <td>SCHOOL STUDENT COUNSELING</td>
        <td>0.56486557</td>
    </tr>
    <tr>
        <td>INTERNATIONAL RELATIONS</td>
        <td>0.632986838</td>
    </tr>
    <tr>
        <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
        <td>0.59420765</td>
    </tr>
    <tr>
        <td>GENERAL AGRICULTURE</td>
        <td>0.515543329</td>
    </tr>
    <tr>
        <td>GENETICS</td>
        <td>0.643331121</td>
    </tr>
    <tr>
        <td>MISCELLANEOUS SOCIAL SCIENCES</td>
        <td>0.5434054220000001</td>
    </tr>
    <tr>
        <td>UNITED STATES HISTORY</td>
        <td>0.6307163179999999</td>
    </tr>
    <tr>
        <td>AGRICULTURAL ECONOMICS</td>
        <td>0.589711902</td>
    </tr>
</table>

```python
# pandas
major_sharewomen = df[lambda df:df["ShareWomen"]>0.5][["Major","ShareWomen"]]
major_sharewomen.head(20)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
      <th>ShareWomen</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>6</th>
      <td>ACTUARIAL SCIENCE</td>
      <td>0.535714</td>
    </tr>
    <tr>
      <th>20</th>
      <td>COMPUTER SCIENCE</td>
      <td>0.578766</td>
    </tr>
    <tr>
      <th>30</th>
      <td>ENVIRONMENTAL ENGINEERING</td>
      <td>0.558548</td>
    </tr>
    <tr>
      <th>34</th>
      <td>NURSING</td>
      <td>0.896019</td>
    </tr>
    <tr>
      <th>38</th>
      <td>INDUSTRIAL PRODUCTION TECHNOLOGIES</td>
      <td>0.750473</td>
    </tr>
    <tr>
      <th>42</th>
      <td>COMPUTER AND INFORMATION SYSTEMS</td>
      <td>0.707719</td>
    </tr>
    <tr>
      <th>45</th>
      <td>INFORMATION SCIENCES</td>
      <td>0.526476</td>
    </tr>
    <tr>
      <th>47</th>
      <td>APPLIED MATHEMATICS</td>
      <td>0.753927</td>
    </tr>
    <tr>
      <th>48</th>
      <td>PHARMACOLOGY</td>
      <td>0.524153</td>
    </tr>
    <tr>
      <th>49</th>
      <td>OCEANOGRAPHY</td>
      <td>0.688999</td>
    </tr>
    <tr>
      <th>52</th>
      <td>MATHEMATICS AND COMPUTER SCIENCE</td>
      <td>0.927807</td>
    </tr>
    <tr>
      <th>54</th>
      <td>COGNITIVE SCIENCE AND BIOPSYCHOLOGY</td>
      <td>0.854523</td>
    </tr>
    <tr>
      <th>55</th>
      <td>SCHOOL STUDENT COUNSELING</td>
      <td>0.564866</td>
    </tr>
    <tr>
      <th>56</th>
      <td>INTERNATIONAL RELATIONS</td>
      <td>0.632987</td>
    </tr>
    <tr>
      <th>63</th>
      <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
      <td>0.594208</td>
    </tr>
    <tr>
      <th>64</th>
      <td>GENERAL AGRICULTURE</td>
      <td>0.515543</td>
    </tr>
    <tr>
      <th>67</th>
      <td>GENETICS</td>
      <td>0.643331</td>
    </tr>
    <tr>
      <th>68</th>
      <td>MISCELLANEOUS SOCIAL SCIENCES</td>
      <td>0.543405</td>
    </tr>
    <tr>
      <th>69</th>
      <td>UNITED STATES HISTORY</td>
      <td>0.630716</td>
    </tr>
    <tr>
      <th>71</th>
      <td>AGRICULTURAL ECONOMICS</td>
      <td>0.589712</td>
    </tr>
  </tbody>
</table>
</div>

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.select { |xs| 
    xs["ShareWomen"] > 0.5
}.map { |xs| 
    [xs["Rank"], xs["Major"]]
}.slice(0..19).map { |xs| 
    p xs
}
```
    [7, "ACTUARIAL SCIENCE"]
    [21, "COMPUTER SCIENCE"]
    [31, "ENVIRONMENTAL ENGINEERING"]
    [35, "NURSING"]
    [39, "INDUSTRIAL PRODUCTION TECHNOLOGIES"]
    [43, "COMPUTER AND INFORMATION SYSTEMS"]
    [46, "INFORMATION SCIENCES"]
    [48, "APPLIED MATHEMATICS"]
    [49, "PHARMACOLOGY"]
    [50, "OCEANOGRAPHY"]
    [53, "MATHEMATICS AND COMPUTER SCIENCE"]
    [55, "COGNITIVE SCIENCE AND BIOPSYCHOLOGY"]
    [56, "SCHOOL STUDENT COUNSELING"]
    [57, "INTERNATIONAL RELATIONS"]
    [64, "AGRICULTURE PRODUCTION AND MANAGEMENT"]
    [65, "GENERAL AGRICULTURE"]
    [68, "GENETICS"]
    [69, "MISCELLANEOUS SOCIAL SCIENCES"]
    [70, "UNITED STATES HISTORY"]
    [72, "AGRICULTURAL ECONOMICS"]


### 就職者数が10000人を超える専攻と就職者数を10件の行を表示する

```python
#sql
%sql SELECT Major,Employed FROM recent_grads WHERE Employed > 10000 limit 10;
```

<table>
    <tr>
        <th>Major</th>
        <th>Employed</th>
    </tr>
    <tr>
        <td>CHEMICAL ENGINEERING</td>
        <td>25694</td>
    </tr>
    <tr>
        <td>MECHANICAL ENGINEERING</td>
        <td>76442</td>
    </tr>
    <tr>
        <td>ELECTRICAL ENGINEERING</td>
        <td>61928</td>
    </tr>
    <tr>
        <td>COMPUTER ENGINEERING</td>
        <td>32506</td>
    </tr>
    <tr>
        <td>AEROSPACE ENGINEERING</td>
        <td>11391</td>
    </tr>
    <tr>
        <td>BIOMEDICAL ENGINEERING</td>
        <td>10047</td>
    </tr>
    <tr>
        <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
        <td>15604</td>
    </tr>
    <tr>
        <td>GENERAL ENGINEERING</td>
        <td>44931</td>
    </tr>
    <tr>
        <td>COMPUTER SCIENCE</td>
        <td>102087</td>
    </tr>
    <tr>
        <td>MANAGEMENT INFORMATION SYSTEMS AND STATISTICS</td>
        <td>16413</td>
    </tr>
</table>

```python
#pandas
major_employed = df[lambda df:df["Employed"]>10000][["Major","Employed"]]
major_employed.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
      <th>Employed</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>4</th>
      <td>CHEMICAL ENGINEERING</td>
      <td>25694</td>
    </tr>
    <tr>
      <th>8</th>
      <td>MECHANICAL ENGINEERING</td>
      <td>76442</td>
    </tr>
    <tr>
      <th>9</th>
      <td>ELECTRICAL ENGINEERING</td>
      <td>61928</td>
    </tr>
    <tr>
      <th>10</th>
      <td>COMPUTER ENGINEERING</td>
      <td>32506</td>
    </tr>
    <tr>
      <th>11</th>
      <td>AEROSPACE ENGINEERING</td>
      <td>11391</td>
    </tr>
    <tr>
      <th>12</th>
      <td>BIOMEDICAL ENGINEERING</td>
      <td>10047</td>
    </tr>
    <tr>
      <th>16</th>
      <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
      <td>15604</td>
    </tr>
    <tr>
      <th>17</th>
      <td>GENERAL ENGINEERING</td>
      <td>44931</td>
    </tr>
    <tr>
      <th>20</th>
      <td>COMPUTER SCIENCE</td>
      <td>102087</td>
    </tr>
    <tr>
      <th>24</th>
      <td>MANAGEMENT INFORMATION SYSTEMS AND STATISTICS</td>
      <td>16413</td>
    </tr>
  </tbody>
</table>
</div>

### 女性率が50%を超えて、かつ従業員が10000人を超える専攻を10行取り出す
Run the query above, which returns all of the female-majority majors with more than 10000 employed graduates

```python
#sql
%sql SELECT Major,ShareWomen,Employed FROM recent_grads WHERE ShareWomen>0.5 AND Employed>10000 LIMIT 10;
```

<table>
    <tr>
        <th>Major</th>
        <th>ShareWomen</th>
        <th>Employed</th>
    </tr>
    <tr>
        <td>COMPUTER SCIENCE</td>
        <td>0.578766338</td>
        <td>102087</td>
    </tr>
    <tr>
        <td>NURSING</td>
        <td>0.896018988</td>
        <td>180903</td>
    </tr>
    <tr>
        <td>COMPUTER AND INFORMATION SYSTEMS</td>
        <td>0.7077185020000001</td>
        <td>28459</td>
    </tr>
    <tr>
        <td>INTERNATIONAL RELATIONS</td>
        <td>0.632986838</td>
        <td>21190</td>
    </tr>
    <tr>
        <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
        <td>0.59420765</td>
        <td>12323</td>
    </tr>
    <tr>
        <td>CHEMISTRY</td>
        <td>0.5051405379999999</td>
        <td>48535</td>
    </tr>
    <tr>
        <td>BUSINESS MANAGEMENT AND ADMINISTRATION</td>
        <td>0.580948004</td>
        <td>276234</td>
    </tr>
    <tr>
        <td>BIOCHEMICAL SCIENCES</td>
        <td>0.515406449</td>
        <td>25678</td>
    </tr>
    <tr>
        <td>HUMAN RESOURCES AND PERSONNEL MANAGEMENT</td>
        <td>0.672161443</td>
        <td>20760</td>
    </tr>
    <tr>
        <td>MISCELLANEOUS HEALTH MEDICAL PROFESSIONS</td>
        <td>0.702020202</td>
        <td>10076</td>
    </tr>
</table>

```python
# pandas
triple = df[lambda df: (df["Employed"]>10000) & (df["ShareWomen"] > 0.5) ][["Major","ShareWomen","Employed"]]
triple.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
      <th>ShareWomen</th>
      <th>Employed</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>20</th>
      <td>COMPUTER SCIENCE</td>
      <td>0.578766</td>
      <td>102087</td>
    </tr>
    <tr>
      <th>34</th>
      <td>NURSING</td>
      <td>0.896019</td>
      <td>180903</td>
    </tr>
    <tr>
      <th>42</th>
      <td>COMPUTER AND INFORMATION SYSTEMS</td>
      <td>0.707719</td>
      <td>28459</td>
    </tr>
    <tr>
      <th>56</th>
      <td>INTERNATIONAL RELATIONS</td>
      <td>0.632987</td>
      <td>21190</td>
    </tr>
    <tr>
      <th>63</th>
      <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
      <td>0.594208</td>
      <td>12323</td>
    </tr>
    <tr>
      <th>74</th>
      <td>CHEMISTRY</td>
      <td>0.505141</td>
      <td>48535</td>
    </tr>
    <tr>
      <th>76</th>
      <td>BUSINESS MANAGEMENT AND ADMINISTRATION</td>
      <td>0.580948</td>
      <td>276234</td>
    </tr>
    <tr>
      <th>82</th>
      <td>BIOCHEMICAL SCIENCES</td>
      <td>0.515406</td>
      <td>25678</td>
    </tr>
    <tr>
      <th>86</th>
      <td>HUMAN RESOURCES AND PERSONNEL MANAGEMENT</td>
      <td>0.672161</td>
      <td>20760</td>
    </tr>
    <tr>
      <th>88</th>
      <td>MISCELLANEOUS HEALTH MEDICAL PROFESSIONS</td>
      <td>0.702020</td>
      <td>10076</td>
    </tr>
  </tbody>
</table>
</div>

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.select { |xs| 
    xs["ShareWomen"] > 0.5 && xs["Employed"] > 10000
}.map { |xs| 
    ["Major","ShareWomen","Employed"].map { |x| xs[x]}
}&.slice(0..9).map { |xs| 
    p xs
}
```

    ["COMPUTER SCIENCE", 0.578766338, 102087]
    ["NURSING", 0.896018988, 180903]
    ["COMPUTER AND INFORMATION SYSTEMS", 0.7077185020000001, 28459]
    ["INTERNATIONAL RELATIONS", 0.632986838, 21190]
    ["AGRICULTURE PRODUCTION AND MANAGEMENT", 0.59420765, 12323]
    ["CHEMISTRY", 0.5051405379999999, 48535]
    ["BUSINESS MANAGEMENT AND ADMINISTRATION", 0.580948004, 276234]
    ["BIOCHEMICAL SCIENCES", 0.515406449, 25678]
    ["HUMAN RESOURCES AND PERSONNEL MANAGEMENT", 0.672161443, 20760]
    ["MISCELLANEOUS HEALTH MEDICAL PROFESSIONS", 0.702020202, 10076]


### 女性率が50%を超え、非雇用率が5.1%未満の"専攻"と"専攻のカテゴリ"について、10行取り出す


```python
#sql
%sql SELECT Major, Major_category, ShareWomen, Unemployment_rate FROM recent_grads where (Major_category = 'Engineering') AND (ShareWomen > 0.5 or Unemployment_rate < 0.051) LIMIT 10;
```

<table>
    <tr>
        <th>Major</th>
        <th>Major_category</th>
        <th>ShareWomen</th>
        <th>Unemployment_rate</th>
    </tr>
    <tr>
        <td>PETROLEUM ENGINEERING</td>
        <td>Engineering</td>
        <td>0.120564344</td>
        <td>0.018380527</td>
    </tr>
    <tr>
        <td>METALLURGICAL ENGINEERING</td>
        <td>Engineering</td>
        <td>0.153037383</td>
        <td>0.024096386</td>
    </tr>
    <tr>
        <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
        <td>Engineering</td>
        <td>0.107313196</td>
        <td>0.050125313</td>
    </tr>
    <tr>
        <td>MATERIALS SCIENCE</td>
        <td>Engineering</td>
        <td>0.310820285</td>
        <td>0.023042836</td>
    </tr>
    <tr>
        <td>ENGINEERING MECHANICS PHYSICS AND SCIENCE</td>
        <td>Engineering</td>
        <td>0.183985189</td>
        <td>0.006334343</td>
    </tr>
    <tr>
        <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
        <td>Engineering</td>
        <td>0.34347321799999997</td>
        <td>0.042875544</td>
    </tr>
    <tr>
        <td>MATERIALS ENGINEERING AND MATERIALS SCIENCE</td>
        <td>Engineering</td>
        <td>0.292607004</td>
        <td>0.027788805</td>
    </tr>
    <tr>
        <td>ENVIRONMENTAL ENGINEERING</td>
        <td>Engineering</td>
        <td>0.558548009</td>
        <td>0.093588575</td>
    </tr>
    <tr>
        <td>INDUSTRIAL PRODUCTION TECHNOLOGIES</td>
        <td>Engineering</td>
        <td>0.75047259</td>
        <td>0.028308097</td>
    </tr>
    <tr>
        <td>ENGINEERING AND INDUSTRIAL MANAGEMENT</td>
        <td>Engineering</td>
        <td>0.174122505</td>
        <td>0.03365166</td>
    </tr>
</table>


```python
triple = df[lambda df:(df["Major_category"] == 'Engineering') & ((df["ShareWomen"] > 0.5) | (df["Unemployment_rate"] < 0.051)) ][["Major", "Major_category", "ShareWomen", "Unemployment_rate"]]
triple.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
      <th>Major_category</th>
      <th>ShareWomen</th>
      <th>Unemployment_rate</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>PETROLEUM ENGINEERING</td>
      <td>Engineering</td>
      <td>0.120564</td>
      <td>0.018381</td>
    </tr>
    <tr>
      <th>2</th>
      <td>METALLURGICAL ENGINEERING</td>
      <td>Engineering</td>
      <td>0.153037</td>
      <td>0.024096</td>
    </tr>
    <tr>
      <th>3</th>
      <td>NAVAL ARCHITECTURE AND MARINE ENGINEERING</td>
      <td>Engineering</td>
      <td>0.107313</td>
      <td>0.050125</td>
    </tr>
    <tr>
      <th>13</th>
      <td>MATERIALS SCIENCE</td>
      <td>Engineering</td>
      <td>0.310820</td>
      <td>0.023043</td>
    </tr>
    <tr>
      <th>14</th>
      <td>ENGINEERING MECHANICS PHYSICS AND SCIENCE</td>
      <td>Engineering</td>
      <td>0.183985</td>
      <td>0.006334</td>
    </tr>
    <tr>
      <th>16</th>
      <td>INDUSTRIAL AND MANUFACTURING ENGINEERING</td>
      <td>Engineering</td>
      <td>0.343473</td>
      <td>0.042876</td>
    </tr>
    <tr>
      <th>23</th>
      <td>MATERIALS ENGINEERING AND MATERIALS SCIENCE</td>
      <td>Engineering</td>
      <td>0.292607</td>
      <td>0.027789</td>
    </tr>
    <tr>
      <th>30</th>
      <td>ENVIRONMENTAL ENGINEERING</td>
      <td>Engineering</td>
      <td>0.558548</td>
      <td>0.093589</td>
    </tr>
    <tr>
      <th>38</th>
      <td>INDUSTRIAL PRODUCTION TECHNOLOGIES</td>
      <td>Engineering</td>
      <td>0.750473</td>
      <td>0.028308</td>
    </tr>
    <tr>
      <th>50</th>
      <td>ENGINEERING AND INDUSTRIAL MANAGEMENT</td>
      <td>Engineering</td>
      <td>0.174123</td>
      <td>0.033652</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.select { |xs| 
    xs["Major_category"] == 'Engineering' && (xs["ShareWomen"] > 0.5 || xs["Unemployment_rate"] < 0.051)
}.map { |xs| 
    ["Major", "Major_category", "ShareWomen", "Unemployment_rate"].map { |x| xs[x]}
}&.slice(0..9).map { |xs| 
    p xs
}
```

    ["PETROLEUM ENGINEERING", "Engineering", 0.120564344, 0.018380527]
    ["METALLURGICAL ENGINEERING", "Engineering", 0.153037383, 0.024096386]
    ["NAVAL ARCHITECTURE AND MARINE ENGINEERING", "Engineering", 0.107313196, 0.050125313]
    ["MATERIALS SCIENCE", "Engineering", 0.310820285, 0.023042836]
    ["ENGINEERING MECHANICS PHYSICS AND SCIENCE", "Engineering", 0.183985189, 0.006334343]
    ["INDUSTRIAL AND MANUFACTURING ENGINEERING", "Engineering", 0.34347321799999997, 0.042875544]
    ["MATERIALS ENGINEERING AND MATERIALS SCIENCE", "Engineering", 0.292607004, 0.027788805]
    ["ENVIRONMENTAL ENGINEERING", "Engineering", 0.558548009, 0.093588575]
    ["INDUSTRIAL PRODUCTION TECHNOLOGIES", "Engineering", 0.75047259, 0.028308097]
    ["ENGINEERING AND INDUSTRIAL MANAGEMENT", "Engineering", 0.174122505, 0.03365166]


### 専攻のカテゴリが”ビジネス”か”芸術”か”ヘルス”で、就職者が20000人を超えているか非雇用率が5.1%以下で、専攻、専攻カテゴリ、就職者数、非就職者を10行知りたい

```python
%sql SELECT Major, Major_category, Employed, Unemployment_rate \
FROM recent_grads \
WHERE (Major_category = 'Business' OR Major_category = 'Arts' OR Major_category = 'Health') \
AND (Employed > 20000 OR Unemployment_rate < 0.051) \
LIMIT 10;
```

<table>
    <tr>
        <th>Major</th>
        <th>Major_category</th>
        <th>Employed</th>
        <th>Unemployment_rate</th>
    </tr>
    <tr>
        <td>OPERATIONS LOGISTICS AND E-COMMERCE</td>
        <td>Business</td>
        <td>10027</td>
        <td>0.047858702999999995</td>
    </tr>
    <tr>
        <td>NURSING</td>
        <td>Health</td>
        <td>180903</td>
        <td>0.04486272400000001</td>
    </tr>
    <tr>
        <td>FINANCE</td>
        <td>Business</td>
        <td>145696</td>
        <td>0.060686356</td>
    </tr>
    <tr>
        <td>ACCOUNTING</td>
        <td>Business</td>
        <td>165527</td>
        <td>0.069749014</td>
    </tr>
    <tr>
        <td>MEDICAL TECHNOLOGIES TECHNICIANS</td>
        <td>Health</td>
        <td>13150</td>
        <td>0.03698279</td>
    </tr>
    <tr>
        <td>MEDICAL ASSISTING SERVICES</td>
        <td>Health</td>
        <td>9168</td>
        <td>0.042506527</td>
    </tr>
    <tr>
        <td>GENERAL BUSINESS</td>
        <td>Business</td>
        <td>190183</td>
        <td>0.072861468</td>
    </tr>
    <tr>
        <td>BUSINESS MANAGEMENT AND ADMINISTRATION</td>
        <td>Business</td>
        <td>276234</td>
        <td>0.07221834099999999</td>
    </tr>
    <tr>
        <td>MARKETING AND MARKETING RESEARCH</td>
        <td>Business</td>
        <td>178862</td>
        <td>0.061215064000000007</td>
    </tr>
    <tr>
        <td>HUMAN RESOURCES AND PERSONNEL MANAGEMENT</td>
        <td>Business</td>
        <td>20760</td>
        <td>0.059569649</td>
    </tr>
</table>

```python
def filt(df):
    res = ((df['Major_category']  == 'Business' ) | (df['Major_category']  == 'Arts') | (df['Major_category']  == 'Health')) & \
                ( (df["Employed"] > 20000 )  |  (df["Unemployment_rate"] < 0.051) )
    return res

quad =  df[ filt(df) ][["Major", "Major_category", "Employed", "Unemployment_rate"]]
quad.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
      <th>Major_category</th>
      <th>Employed</th>
      <th>Unemployment_rate</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>27</th>
      <td>OPERATIONS LOGISTICS AND E-COMMERCE</td>
      <td>Business</td>
      <td>10027</td>
      <td>0.047859</td>
    </tr>
    <tr>
      <th>34</th>
      <td>NURSING</td>
      <td>Health</td>
      <td>180903</td>
      <td>0.044863</td>
    </tr>
    <tr>
      <th>35</th>
      <td>FINANCE</td>
      <td>Business</td>
      <td>145696</td>
      <td>0.060686</td>
    </tr>
    <tr>
      <th>40</th>
      <td>ACCOUNTING</td>
      <td>Business</td>
      <td>165527</td>
      <td>0.069749</td>
    </tr>
    <tr>
      <th>44</th>
      <td>MEDICAL TECHNOLOGIES TECHNICIANS</td>
      <td>Health</td>
      <td>13150</td>
      <td>0.036983</td>
    </tr>
    <tr>
      <th>51</th>
      <td>MEDICAL ASSISTING SERVICES</td>
      <td>Health</td>
      <td>9168</td>
      <td>0.042507</td>
    </tr>
    <tr>
      <th>57</th>
      <td>GENERAL BUSINESS</td>
      <td>Business</td>
      <td>190183</td>
      <td>0.072861</td>
    </tr>
    <tr>
      <th>76</th>
      <td>BUSINESS MANAGEMENT AND ADMINISTRATION</td>
      <td>Business</td>
      <td>276234</td>
      <td>0.072218</td>
    </tr>
    <tr>
      <th>77</th>
      <td>MARKETING AND MARKETING RESEARCH</td>
      <td>Business</td>
      <td>178862</td>
      <td>0.061215</td>
    </tr>
    <tr>
      <th>86</th>
      <td>HUMAN RESOURCES AND PERSONNEL MANAGEMENT</td>
      <td>Business</td>
      <td>20760</td>
      <td>0.059570</td>
    </tr>
  </tbody>
</table>
</div>

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.select { |xs| 
    (xs["Major_category"] == 'Engineering' ||  xs["Major_category"] == 'Arts' || xs["Major_category"] == 'Health' ) && (xs["Employed"] > 20000 || xs["Unemployment_rate"] < 0.051)
}.map { |xs| 
    ["Major", "Major_category", "Employed", "Unemployment_rate"].map { |x| xs[x]}
}.slice(0..9).map { |xs| 
    p xs
}
```

    ["PETROLEUM ENGINEERING", "Engineering", 1976, 0.018380527]
    ["METALLURGICAL ENGINEERING", "Engineering", 648, 0.024096386]
    ["NAVAL ARCHITECTURE AND MARINE ENGINEERING", "Engineering", 758, 0.050125313]
    ["CHEMICAL ENGINEERING", "Engineering", 25694, 0.061097712]
    ["MECHANICAL ENGINEERING", "Engineering", 76442, 0.057342277999999997]
    ["ELECTRICAL ENGINEERING", "Engineering", 61928, 0.059173845]
    ["COMPUTER ENGINEERING", "Engineering", 32506, 0.065409275]
    ["MATERIALS SCIENCE", "Engineering", 3307, 0.023042836]
    ["ENGINEERING MECHANICS PHYSICS AND SCIENCE", "Engineering", 3608, 0.006334343]
    ["INDUSTRIAL AND MANUFACTURING ENGINEERING", "Engineering", 15604, 0.042875544]


###  専攻をアルファベットを降順にソートして10行取り出す

```python
#sql
%sql select Major \
from recent_grads \
order by Major desc \
limit 10;
```

<table>
    <tr>
        <th>Major</th>
    </tr>
    <tr>
        <td>ZOOLOGY</td>
    </tr>
    <tr>
        <td>VISUAL AND PERFORMING ARTS</td>
    </tr>
    <tr>
        <td>UNITED STATES HISTORY</td>
    </tr>
    <tr>
        <td>TREATMENT THERAPY PROFESSIONS</td>
    </tr>
    <tr>
        <td>TRANSPORTATION SCIENCES AND TECHNOLOGIES</td>
    </tr>
    <tr>
        <td>THEOLOGY AND RELIGIOUS VOCATIONS</td>
    </tr>
    <tr>
        <td>TEACHER EDUCATION: MULTIPLE LEVELS</td>
    </tr>
    <tr>
        <td>STUDIO ARTS</td>
    </tr>
    <tr>
        <td>STATISTICS AND DECISION SCIENCE</td>
    </tr>
    <tr>
        <td>SPECIAL NEEDS EDUCATION</td>
    </tr>
</table>

```python
asd = df[["Major"]].sort_values(by=["Major"], ascending=False)
asd.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>168</th>
      <td>ZOOLOGY</td>
    </tr>
    <tr>
      <th>153</th>
      <td>VISUAL AND PERFORMING ARTS</td>
    </tr>
    <tr>
      <th>69</th>
      <td>UNITED STATES HISTORY</td>
    </tr>
    <tr>
      <th>126</th>
      <td>TREATMENT THERAPY PROFESSIONS</td>
    </tr>
    <tr>
      <th>106</th>
      <td>TRANSPORTATION SCIENCES AND TECHNOLOGIES</td>
    </tr>
    <tr>
      <th>158</th>
      <td>THEOLOGY AND RELIGIOUS VOCATIONS</td>
    </tr>
    <tr>
      <th>154</th>
      <td>TEACHER EDUCATION: MULTIPLE LEVELS</td>
    </tr>
    <tr>
      <th>159</th>
      <td>STUDIO ARTS</td>
    </tr>
    <tr>
      <th>46</th>
      <td>STATISTICS AND DECISION SCIENCE</td>
    </tr>
    <tr>
      <th>100</th>
      <td>SPECIAL NEEDS EDUCATION</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.sort_by { |xs|
    xs["Major"] 
}.reverse
.slice(0..9).map { |xs| p xs["Major"]}
```
    "ZOOLOGY"
    "VISUAL AND PERFORMING ARTS"
    "UNITED STATES HISTORY"
    "TREATMENT THERAPY PROFESSIONS"
    "TRANSPORTATION SCIENCES AND TECHNOLOGIES"
    "THEOLOGY AND RELIGIOUS VOCATIONS"
    "TEACHER EDUCATION: MULTIPLE LEVELS"
    "STUDIO ARTS"
    "STATISTICS AND DECISION SCIENCE"
    "SPECIAL NEEDS EDUCATION"


### 専攻をアルファベットで昇順、給与で降順で、20行を表示する

```python
#sql
%sql SELECT Major_category, Median, Major \
FROM recent_grads \
ORDER BY Major ASC, Median DESC \
LIMIT 20;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>Median</th>
        <th>Major</th>
    </tr>
    <tr>
        <td>Business</td>
        <td>45000</td>
        <td>ACCOUNTING</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>62000</td>
        <td>ACTUARIAL SCIENCE</td>
    </tr>
    <tr>
        <td>Communications &amp; Journalism</td>
        <td>35000</td>
        <td>ADVERTISING AND PUBLIC RELATIONS</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>60000</td>
        <td>AEROSPACE ENGINEERING</td>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>40000</td>
        <td>AGRICULTURAL ECONOMICS</td>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>40000</td>
        <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>30000</td>
        <td>ANIMAL SCIENCES</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>28000</td>
        <td>ANTHROPOLOGY AND ARCHEOLOGY</td>
    </tr>
    <tr>
        <td>Computers &amp; Mathematics</td>
        <td>45000</td>
        <td>APPLIED MATHEMATICS</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>54000</td>
        <td>ARCHITECTURAL ENGINEERING</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>40000</td>
        <td>ARCHITECTURE</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>35000</td>
        <td>AREA ETHNIC AND CIVILIZATION STUDIES</td>
    </tr>
    <tr>
        <td>Education</td>
        <td>32100</td>
        <td>ART AND MUSIC EDUCATION</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>31000</td>
        <td>ART HISTORY AND CRITICISM</td>
    </tr>
    <tr>
        <td>Physical Sciences</td>
        <td>62000</td>
        <td>ASTRONOMY AND ASTROPHYSICS</td>
    </tr>
    <tr>
        <td>Physical Sciences</td>
        <td>35000</td>
        <td>ATMOSPHERIC SCIENCES AND METEOROLOGY</td>
    </tr>
    <tr>
        <td>Biology &amp; Life Science</td>
        <td>37400</td>
        <td>BIOCHEMICAL SCIENCES</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>57100</td>
        <td>BIOLOGICAL ENGINEERING</td>
    </tr>
    <tr>
        <td>Biology &amp; Life Science</td>
        <td>33400</td>
        <td>BIOLOGY</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>60000</td>
        <td>BIOMEDICAL ENGINEERING</td>
    </tr>
</table>


```python
tri = df[["Major_category", "Median", "Major"]].sort_values(by=["Major", "Median"], ascending=[True, False])
tri.head(20)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major_category</th>
      <th>Median</th>
      <th>Major</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>40</th>
      <td>Business</td>
      <td>45000</td>
      <td>ACCOUNTING</td>
    </tr>
    <tr>
      <th>6</th>
      <td>Business</td>
      <td>62000</td>
      <td>ACTUARIAL SCIENCE</td>
    </tr>
    <tr>
      <th>98</th>
      <td>Communications &amp; Journalism</td>
      <td>35000</td>
      <td>ADVERTISING AND PUBLIC RELATIONS</td>
    </tr>
    <tr>
      <th>11</th>
      <td>Engineering</td>
      <td>60000</td>
      <td>AEROSPACE ENGINEERING</td>
    </tr>
    <tr>
      <th>71</th>
      <td>Agriculture &amp; Natural Resources</td>
      <td>40000</td>
      <td>AGRICULTURAL ECONOMICS</td>
    </tr>
    <tr>
      <th>63</th>
      <td>Agriculture &amp; Natural Resources</td>
      <td>40000</td>
      <td>AGRICULTURE PRODUCTION AND MANAGEMENT</td>
    </tr>
    <tr>
      <th>152</th>
      <td>Agriculture &amp; Natural Resources</td>
      <td>30000</td>
      <td>ANIMAL SCIENCES</td>
    </tr>
    <tr>
      <th>162</th>
      <td>Humanities &amp; Liberal Arts</td>
      <td>28000</td>
      <td>ANTHROPOLOGY AND ARCHEOLOGY</td>
    </tr>
    <tr>
      <th>47</th>
      <td>Computers &amp; Mathematics</td>
      <td>45000</td>
      <td>APPLIED MATHEMATICS</td>
    </tr>
    <tr>
      <th>18</th>
      <td>Engineering</td>
      <td>54000</td>
      <td>ARCHITECTURAL ENGINEERING</td>
    </tr>
    <tr>
      <th>58</th>
      <td>Engineering</td>
      <td>40000</td>
      <td>ARCHITECTURE</td>
    </tr>
    <tr>
      <th>99</th>
      <td>Humanities &amp; Liberal Arts</td>
      <td>35000</td>
      <td>AREA ETHNIC AND CIVILIZATION STUDIES</td>
    </tr>
    <tr>
      <th>136</th>
      <td>Education</td>
      <td>32100</td>
      <td>ART AND MUSIC EDUCATION</td>
    </tr>
    <tr>
      <th>148</th>
      <td>Humanities &amp; Liberal Arts</td>
      <td>31000</td>
      <td>ART HISTORY AND CRITICISM</td>
    </tr>
    <tr>
      <th>7</th>
      <td>Physical Sciences</td>
      <td>62000</td>
      <td>ASTRONOMY AND ASTROPHYSICS</td>
    </tr>
    <tr>
      <th>110</th>
      <td>Physical Sciences</td>
      <td>35000</td>
      <td>ATMOSPHERIC SCIENCES AND METEOROLOGY</td>
    </tr>
    <tr>
      <th>82</th>
      <td>Biology &amp; Life Science</td>
      <td>37400</td>
      <td>BIOCHEMICAL SCIENCES</td>
    </tr>
    <tr>
      <th>15</th>
      <td>Engineering</td>
      <td>57100</td>
      <td>BIOLOGICAL ENGINEERING</td>
    </tr>
    <tr>
      <th>123</th>
      <td>Biology &amp; Life Science</td>
      <td>33400</td>
      <td>BIOLOGY</td>
    </tr>
    <tr>
      <th>12</th>
      <td>Engineering</td>
      <td>60000</td>
      <td>BIOMEDICAL ENGINEERING</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs|
   cols.zip(xs).to_h
}.sort_by { |as, bs|
    [as["Major"],  !as["Median"] ]
}.slice(0..19).map { |xs| p [xs["Major"], xs["Median"]]}
```

    ["ACCOUNTING", 45000]
    ["ACTUARIAL SCIENCE", 62000]
    ["ADVERTISING AND PUBLIC RELATIONS", 35000]
    ["AEROSPACE ENGINEERING", 60000]
    ["AGRICULTURAL ECONOMICS", 40000]
    ["AGRICULTURE PRODUCTION AND MANAGEMENT", 40000]
    ["ANIMAL SCIENCES", 30000]
    ["ANTHROPOLOGY AND ARCHEOLOGY", 28000]
    ["APPLIED MATHEMATICS", 45000]
    ["ARCHITECTURAL ENGINEERING", 54000]
    ["ARCHITECTURE", 40000]
    ["AREA ETHNIC AND CIVILIZATION STUDIES", 35000]
    ["ART AND MUSIC EDUCATION", 32100]
    ["ART HISTORY AND CRITICISM", 31000]
    ["ASTRONOMY AND ASTROPHYSICS", 62000]
    ["ATMOSPHERIC SCIENCES AND METEOROLOGY", 35000]
    ["BIOCHEMICAL SCIENCES", 37400]
    ["BIOLOGICAL ENGINEERING", 57100]
    ["BIOLOGY", 33400]
    ["BIOMEDICAL ENGINEERING", 60000]

## FactBook Dataset
factbookと呼ばれるデータセットで、SQLとpandas, Rubyでの記述を示します

```python
# まずSQLITEをメモリにロード
%sql sqlite:////var/factbook.db
# pythonのdataframeにロード
conn = sqlite3.connect("/var/factbook.db")
df = pd.read_sql_query("select * from facts;", conn)
```

### データに入っているbirth_rateの件数をカウント

```python
# sql
%sql SELECT COUNT(birth_rate) FROM facts;
```

<table>
    <tr>
        <th>COUNT(birth_rate)</th>
    </tr>
    <tr>
        <td>228</td>
    </tr>
</table>


```python
# pandas
df["birth_rate"].count()
```

    228

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/factbook.db"
cols = db.execute("PRAGMA table_info(facts)").map { |xs| xs[1]}
r = db.execute( "SELECT * FROM facts ;" ).map {  |xs|
   cols.zip(xs).to_h
}.select { |xs| 
    xs["birth_rate"] != nil
}.size
p r
```

    228


### birth_rateの合計値を計算


```python
#sql
%sql SELECT SUM(birth_rate) \
FROM facts;
```

<table>
    <tr>
        <th>SUM(birth_rate)</th>
    </tr>
    <tr>
        <td>4406.909999999998</td>
    </tr>
</table>


```python
# pandas
df["birth_rate"].sum()
```

    4406.9099999999999

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/factbook.db"
cols = db.execute("PRAGMA table_info(facts)").map { |xs| xs[1]}
r = db.execute( "SELECT * FROM facts ;" ).map {  |xs| cols.zip(xs).to_h
}.select { |xs| xs["birth_rate"] != nil
}.map { |x| x["birth_rate"]
}.reduce { |y,x| y+x }
p r
```

    4406.909999999998


### birth_rateの平均値の計算


```python
%sql SELECT AVG(birth_rate) \
FROM facts;
```

<table>
    <tr>
        <th>AVG(birth_rate)</th>
    </tr>
    <tr>
        <td>19.32855263157894</td>
    </tr>
</table>

```python
df["birth_rate"].mean()
```

    19.328552631578948

```ruby
%%ruby
require "sqlite3"
db = SQLite3::Database.new "/var/factbook.db"
cols = db.execute("PRAGMA table_info(facts)").map { |xs| xs[1]}
all = db.execute( "SELECT * FROM facts ;" ).map {  |xs| cols.zip(xs).to_h
}.select { |xs| xs["birth_rate"] != nil }.map { |x| x["birth_rate"] }
p all.reduce{|y,x| y+x }/all.size
```

    19.32855263157894


### 出生率をUniq(Distinct)して、人口が2000万を超えるデータの平均値を計算する


```python
# sql
%sql SELECT AVG(DISTINCT birth_rate) \
FROM facts \
WHERE population > 20000000;
```

<table>
    <tr>
        <th>AVG(DISTINCT birth_rate)</th>
    </tr>
    <tr>
        <td>20.43473684210527</td>
    </tr>
</table>

```python
# pandas
df[ df.population > 20000000 ][ ["birth_rate"] ].drop_duplicates().mean()
```

    birth_rate    20.434737
    dtype: float64

```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/factbook.db"
cols = db.execute("PRAGMA table_info(facts)").map { |xs| xs[1]}
all = db.execute( "SELECT * FROM facts ;" ).map {  |xs| cols.zip(xs).to_h
}.select { |xs|  xs["population"] != nil and xs["population"] > 20000000 
}.map { |xs| xs["birth_rate"] }.to_set.to_a
p all.reduce { |y,x| y+x}/all.size
```

    20.43473684210527


## より細かい条件指定
ここから、データセットをjobs.dbに戻します  
groupbyなど一歩踏み込んだSQLのオペレーションであっても、Pandasや関数型言語でも同様に扱えることを示します


```python
# sqliteをメモリにロード
%sql sqlite:////var/jobs.db
# pythonのdataframeにロード
conn = sqlite3.connect("/var/jobs.db")
df = pd.read_sql_query("select * from recent_grads;", conn)
```

### 専攻カテゴリごとにおける、女性率の平均値の計算


```python
#sql
%sql SELECT Major_category, AVG(ShareWomen) \
FROM recent_grads \
GROUP BY Major_category;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>AVG(ShareWomen)</th>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>0.6179384232</td>
    </tr>
    <tr>
        <td>Arts</td>
        <td>0.56185119575</td>
    </tr>
    <tr>
        <td>Biology &amp; Life Science</td>
        <td>0.584518475857143</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>0.4050631853076923</td>
    </tr>
    <tr>
        <td>Communications &amp; Journalism</td>
        <td>0.64383484025</td>
    </tr>
    <tr>
        <td>Computers &amp; Mathematics</td>
        <td>0.5127519954545455</td>
    </tr>
    <tr>
        <td>Education</td>
        <td>0.6749855163125</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.2571578951034483</td>
    </tr>
    <tr>
        <td>Health</td>
        <td>0.6168565694166667</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>0.6761934042</td>
    </tr>
    <tr>
        <td>Industrial Arts &amp; Consumer Services</td>
        <td>0.4493512688571429</td>
    </tr>
    <tr>
        <td>Interdisciplinary</td>
        <td>0.495397153</td>
    </tr>
    <tr>
        <td>Law &amp; Public Policy</td>
        <td>0.3359896912</td>
    </tr>
    <tr>
        <td>Physical Sciences</td>
        <td>0.5087494197</td>
    </tr>
    <tr>
        <td>Psychology &amp; Social Work</td>
        <td>0.7777631628888888</td>
    </tr>
    <tr>
        <td>Social Science</td>
        <td>0.5390672957777778</td>
    </tr>
</table>


```python
#  pandas
df[ ["Major_category", "ShareWomen"] ].groupby( ["Major_category"]).mean()
```


<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>ShareWomen</th>
    </tr>
    <tr>
      <th>Major_category</th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>Agriculture &amp; Natural Resources</th>
      <td>0.617938</td>
    </tr>
    <tr>
      <th>Arts</th>
      <td>0.561851</td>
    </tr>
    <tr>
      <th>Biology &amp; Life Science</th>
      <td>0.584518</td>
    </tr>
    <tr>
      <th>Business</th>
      <td>0.405063</td>
    </tr>
    <tr>
      <th>Communications &amp; Journalism</th>
      <td>0.643835</td>
    </tr>
    <tr>
      <th>Computers &amp; Mathematics</th>
      <td>0.512752</td>
    </tr>
    <tr>
      <th>Education</th>
      <td>0.674986</td>
    </tr>
    <tr>
      <th>Engineering</th>
      <td>0.257158</td>
    </tr>
    <tr>
      <th>Health</th>
      <td>0.616857</td>
    </tr>
    <tr>
      <th>Humanities &amp; Liberal Arts</th>
      <td>0.676193</td>
    </tr>
    <tr>
      <th>Industrial Arts &amp; Consumer Services</th>
      <td>0.449351</td>
    </tr>
    <tr>
      <th>Interdisciplinary</th>
      <td>0.495397</td>
    </tr>
    <tr>
      <th>Law &amp; Public Policy</th>
      <td>0.335990</td>
    </tr>
    <tr>
      <th>Physical Sciences</th>
      <td>0.508749</td>
    </tr>
    <tr>
      <th>Psychology &amp; Social Work</th>
      <td>0.777763</td>
    </tr>
    <tr>
      <th>Social Science</th>
      <td>0.539067</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs| cols.zip(xs).to_h
}.map { |xs| ["Major_category", "ShareWomen"].map {|x| xs[x]} 
}.group_by { |xs|  xs[0]}.to_a
.map { |xs| 
    key,vals = xs
    vals = vals.map { |x| x[1]}
    [key, vals.reduce{ |y,x| y+x}/vals.size]
}.map { |xs| 
    p xs
}
```

    ["Engineering", 0.2571578951034483]
    ["Business", 0.4050631853076923]
    ["Physical Sciences", 0.5087494197]
    ["Law & Public Policy", 0.3359896912]
    ["Computers & Mathematics", 0.5127519954545455]
    ["Agriculture & Natural Resources", 0.6179384232]
    ["Industrial Arts & Consumer Services", 0.4493512688571429]
    ["Arts", 0.56185119575]
    ["Health", 0.6168565694166667]
    ["Social Science", 0.5390672957777778]
    ["Biology & Life Science", 0.584518475857143]
    ["Education", 0.6749855163125]
    ["Humanities & Liberal Arts", 0.6761934042]
    ["Psychology & Social Work", 0.7777631628888888]
    ["Communications & Journalism", 0.64383484025]
    ["Interdisciplinary", 0.495397153]


### 専攻カテゴリごとの平均就職者数、全人数の平均値を計算して、"就職者数/平均人数"を算出する

```python
#SQL
%sql SELECT Major_category, AVG(Employed) / AVG(Total) AS share_employed \
FROM recent_grads \
GROUP BY Major_category;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>share_employed</th>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>0.8369862842425075</td>
    </tr>
    <tr>
        <td>Arts</td>
        <td>0.8067482429367457</td>
    </tr>
    <tr>
        <td>Biology &amp; Life Science</td>
        <td>0.6671565365683841</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>0.8359659576036412</td>
    </tr>
    <tr>
        <td>Communications &amp; Journalism</td>
        <td>0.8422291333949735</td>
    </tr>
    <tr>
        <td>Computers &amp; Mathematics</td>
        <td>0.7956108197773972</td>
    </tr>
    <tr>
        <td>Education</td>
        <td>0.858190149321534</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.7819666916550562</td>
    </tr>
    <tr>
        <td>Health</td>
        <td>0.8033741337996244</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>0.7626382682895378</td>
    </tr>
    <tr>
        <td>Industrial Arts &amp; Consumer Services</td>
        <td>0.8226700668430581</td>
    </tr>
    <tr>
        <td>Interdisciplinary</td>
        <td>0.7987150292778139</td>
    </tr>
    <tr>
        <td>Law &amp; Public Policy</td>
        <td>0.8083994483744353</td>
    </tr>
    <tr>
        <td>Physical Sciences</td>
        <td>0.7506564085422069</td>
    </tr>
    <tr>
        <td>Psychology &amp; Social Work</td>
        <td>0.790724459311403</td>
    </tr>
    <tr>
        <td>Social Science</td>
        <td>0.7575825619001975</td>
    </tr>
</table>


```python
# pandas
df_mean = df[ ["Major_category", "Employed", "Total"] ].groupby( ["Major_category"]).mean()
df_mean[ "Employed/Total" ] = df_mean.apply(lambda x:x[0]/x[1],axis=1)
df_mean
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Employed</th>
      <th>Total</th>
      <th>Employed/Total</th>
    </tr>
    <tr>
      <th>Major_category</th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>Agriculture &amp; Natural Resources</th>
      <td>6694.300000</td>
      <td>7998.100000</td>
      <td>0.836986</td>
    </tr>
    <tr>
      <th>Arts</th>
      <td>36014.250000</td>
      <td>44641.250000</td>
      <td>0.806748</td>
    </tr>
    <tr>
      <th>Biology &amp; Life Science</th>
      <td>21628.357143</td>
      <td>32418.714286</td>
      <td>0.667157</td>
    </tr>
    <tr>
      <th>Business</th>
      <td>83749.384615</td>
      <td>100182.769231</td>
      <td>0.835966</td>
    </tr>
    <tr>
      <th>Communications &amp; Journalism</th>
      <td>82665.000000</td>
      <td>98150.250000</td>
      <td>0.842229</td>
    </tr>
    <tr>
      <th>Computers &amp; Mathematics</th>
      <td>21626.727273</td>
      <td>27182.545455</td>
      <td>0.795611</td>
    </tr>
    <tr>
      <th>Education</th>
      <td>29989.937500</td>
      <td>34945.562500</td>
      <td>0.858190</td>
    </tr>
    <tr>
      <th>Engineering</th>
      <td>14495.586207</td>
      <td>18537.344828</td>
      <td>0.781967</td>
    </tr>
    <tr>
      <th>Health</th>
      <td>31012.250000</td>
      <td>38602.500000</td>
      <td>0.803374</td>
    </tr>
    <tr>
      <th>Humanities &amp; Liberal Arts</th>
      <td>36274.533333</td>
      <td>47564.533333</td>
      <td>0.762638</td>
    </tr>
    <tr>
      <th>Industrial Arts &amp; Consumer Services</th>
      <td>27006.142857</td>
      <td>32827.428571</td>
      <td>0.822670</td>
    </tr>
    <tr>
      <th>Interdisciplinary</th>
      <td>9821.000000</td>
      <td>12296.000000</td>
      <td>0.798715</td>
    </tr>
    <tr>
      <th>Law &amp; Public Policy</th>
      <td>28958.000000</td>
      <td>35821.400000</td>
      <td>0.808399</td>
    </tr>
    <tr>
      <th>Physical Sciences</th>
      <td>13923.100000</td>
      <td>18547.900000</td>
      <td>0.750656</td>
    </tr>
    <tr>
      <th>Psychology &amp; Social Work</th>
      <td>42260.444444</td>
      <td>53445.222222</td>
      <td>0.790724</td>
    </tr>
    <tr>
      <th>Social Science</th>
      <td>44610.333333</td>
      <td>58885.111111</td>
      <td>0.757583</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs| cols.zip(xs).to_h
}.map { |xs| ["Major_category", "Employed", "Total"].map {|x| xs[x]} 
}.group_by { |xs|  xs[0]}.to_a
.map { |xs| 
    key,vals = xs
    emps = vals.map { |x| x[1]}
    emps_mean = emps.reduce{ |y,x| y+x}/emps.size 
    totals = vals.map { |x| x[2]}
    totals_mean = totals.reduce{ |y,x| y+x}/totals.size
    [key, emps_mean.to_f/totals_mean]
}.map { |xs| 
    p xs
}
```

    ["Engineering", 0.7819496142849436]
    ["Business", 0.8359685372621828]
    ["Physical Sciences", 0.7506874427131073]
    ["Law & Public Policy", 0.8084084754752798]
    ["Computers & Mathematics", 0.7956000294312413]
    ["Agriculture & Natural Resources", 0.8369592398099525]
    ["Industrial Arts & Consumer Services", 0.8226764553568708]
    ["Arts", 0.8067471606818843]
    ["Health", 0.8033780633127817]
    ["Social Science", 0.7575783306444765]
    ["Biology & Life Science", 0.6671602196310692]
    ["Education", 0.8581771354986407]
    ["Humanities & Liberal Arts", 0.7626356067614162]
    ["Psychology & Social Work", 0.790719431190944]
    ["Communications & Journalism", 0.8422312786551197]
    ["Interdisciplinary", 0.7987150292778139]


### 先ほど計算した、"就職者数/平均人数"が0.8を超えるデータを表示する


```python
#SQL
%sql SELECT Major_category, AVG(Employed) / AVG(Total) AS share_employed \
FROM recent_grads \
GROUP BY Major_category \
    HAVING share_employed > .8;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>share_employed</th>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>0.8369862842425075</td>
    </tr>
    <tr>
        <td>Arts</td>
        <td>0.8067482429367457</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>0.8359659576036412</td>
    </tr>
    <tr>
        <td>Communications &amp; Journalism</td>
        <td>0.8422291333949735</td>
    </tr>
    <tr>
        <td>Education</td>
        <td>0.858190149321534</td>
    </tr>
    <tr>
        <td>Health</td>
        <td>0.8033741337996244</td>
    </tr>
    <tr>
        <td>Industrial Arts &amp; Consumer Services</td>
        <td>0.8226700668430581</td>
    </tr>
    <tr>
        <td>Law &amp; Public Policy</td>
        <td>0.8083994483744353</td>
    </tr>
</table>


```python
# pandas
df_having = df[ ["Major_category", "Employed", "Total"] ].groupby( ["Major_category"]).mean()
df_having[ "Employed/Total" ] = df_mean.apply(lambda x:x[0]/x[1],axis=1)
df_having[ df_having["Employed/Total"] > 0.8 ]
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Employed</th>
      <th>Total</th>
      <th>Employed/Total</th>
    </tr>
    <tr>
      <th>Major_category</th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>Agriculture &amp; Natural Resources</th>
      <td>6694.300000</td>
      <td>7998.100000</td>
      <td>0.836986</td>
    </tr>
    <tr>
      <th>Arts</th>
      <td>36014.250000</td>
      <td>44641.250000</td>
      <td>0.806748</td>
    </tr>
    <tr>
      <th>Business</th>
      <td>83749.384615</td>
      <td>100182.769231</td>
      <td>0.835966</td>
    </tr>
    <tr>
      <th>Communications &amp; Journalism</th>
      <td>82665.000000</td>
      <td>98150.250000</td>
      <td>0.842229</td>
    </tr>
    <tr>
      <th>Education</th>
      <td>29989.937500</td>
      <td>34945.562500</td>
      <td>0.858190</td>
    </tr>
    <tr>
      <th>Health</th>
      <td>31012.250000</td>
      <td>38602.500000</td>
      <td>0.803374</td>
    </tr>
    <tr>
      <th>Industrial Arts &amp; Consumer Services</th>
      <td>27006.142857</td>
      <td>32827.428571</td>
      <td>0.822670</td>
    </tr>
    <tr>
      <th>Law &amp; Public Policy</th>
      <td>28958.000000</td>
      <td>35821.400000</td>
      <td>0.808399</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs| cols.zip(xs).to_h
}.map { |xs| ["Major_category", "Employed", "Total"].map {|x| xs[x]} 
}.group_by { |xs|  xs[0]}.to_a
.map { |xs| 
    key,vals = xs
    emps = vals.map { |x| x[1]}
    emps_mean = emps.reduce{ |y,x| y+x}/emps.size 
    totals = vals.map { |x| x[2]}
    totals_mean = totals.reduce{ |y,x| y+x}/totals.size
    [key, emps_mean.to_f/totals_mean]
}.select{ |xs| xs[1] > 0.8
}.map { |xs| 
    p xs
}
```

    ["Business", 0.8359685372621828]
    ["Law & Public Policy", 0.8084084754752798]
    ["Agriculture & Natural Resources", 0.8369592398099525]
    ["Industrial Arts & Consumer Services", 0.8226764553568708]
    ["Arts", 0.8067471606818843]
    ["Health", 0.8033780633127817]
    ["Education", 0.8581771354986407]
    ["Communications & Journalism", 0.8422312786551197]


### 女性率を小数点以下、第二桁まで計算して１０行を表示


```python
#SQL
%sql SELECT Major_category, ROUND(ShareWomen, 2) AS rounded_share_women \
FROM recent_grads \
LIMIT 10;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>rounded_share_women</th>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.12</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.1</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.15</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.11</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.34</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.14</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>0.54</td>
    </tr>
    <tr>
        <td>Physical Sciences</td>
        <td>0.44</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.14</td>
    </tr>
    <tr>
        <td>Engineering</td>
        <td>0.44</td>
    </tr>
</table>


```python
# pandas
df_round = df[ ["Major_category", "ShareWomen"] ]
df_round["round"]=  df_round.apply(lambda x:"%0.2f"%x[1], axis=1)
df_round.head(10)
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Major_category</th>
      <th>ShareWomen</th>
      <th>round</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>Engineering</td>
      <td>0.120564</td>
      <td>0.12</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Engineering</td>
      <td>0.101852</td>
      <td>0.10</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Engineering</td>
      <td>0.153037</td>
      <td>0.15</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Engineering</td>
      <td>0.107313</td>
      <td>0.11</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Engineering</td>
      <td>0.341631</td>
      <td>0.34</td>
    </tr>
    <tr>
      <th>5</th>
      <td>Engineering</td>
      <td>0.144967</td>
      <td>0.14</td>
    </tr>
    <tr>
      <th>6</th>
      <td>Business</td>
      <td>0.535714</td>
      <td>0.54</td>
    </tr>
    <tr>
      <th>7</th>
      <td>Physical Sciences</td>
      <td>0.441356</td>
      <td>0.44</td>
    </tr>
    <tr>
      <th>8</th>
      <td>Engineering</td>
      <td>0.139793</td>
      <td>0.14</td>
    </tr>
    <tr>
      <th>9</th>
      <td>Engineering</td>
      <td>0.437847</td>
      <td>0.44</td>
    </tr>
  </tbody>
</table>
</div>


```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs| cols.zip(xs).to_h
}.map { |xs| ["Major_category", "ShareWomen"].map {|x| xs[x]} 
}.map { |xs| 
    mc = xs[0]
    sw = sprintf("%0.2f", xs[1])
    [mc, sw]
}.map{ |x| p x}
```

    ["Engineering", "0.12"]
    ["Engineering", "0.10"]
    ["Engineering", "0.15"]
    ["Engineering", "0.11"]
    ["Engineering", "0.34"]
    ["Engineering", "0.14"]
    ["Business", "0.54"]
    ["Physical Sciences", "0.44"]
    ["Engineering", "0.14"]
    ["Engineering", "0.44"]
    ["Engineering", "0.20"]
    ["Engineering", "0.20"]
    ["Engineering", "0.12"]
    ["Engineering", "0.31"]
    ["Engineering", "0.18"]
    ["Engineering", "0.32"]
    ["Engineering", "0.34"]
    ["Engineering", "0.25"]
    ["Engineering", "0.35"]
    ["Law & Public Policy", "0.24"]
    ["Computers & Mathematics", "0.58"]
    ["Agriculture & Natural Resources", "0.22"]
    ["Engineering", "0.33"]
    ["Engineering", "0.29"]
    ["Business", "0.28"]
    ["Engineering", "0.23"]
    ["Industrial Arts & Consumer Services", "0.34"]
    ["Business", "0.32"]
    ["Engineering", "0.19"]
    ["Law & Public Policy", "0.25"]
    ["Engineering", "0.56"]
    ["Engineering", "0.09"]
    ["Arts", "0.41"]
    ["Engineering", "0.32"]
    ["Health", "0.90"]
    ["Business", "0.36"]
    ["Social Science", "0.34"]
    ["Business", "0.25"]
    ["Engineering", "0.75"]
    ["Physical Sciences", "0.43"]
    ["Business", "0.25"]
    ["Computers & Mathematics", "0.24"]
    ["Computers & Mathematics", "0.71"]
    ["Physical Sciences", "0.45"]
    ["Health", "0.43"]
    ["Computers & Mathematics", "0.53"]
    ["Computers & Mathematics", "0.28"]
    ["Computers & Mathematics", "0.75"]
    ["Biology & Life Science", "0.52"]
    ["Physical Sciences", "0.69"]
    ["Engineering", "0.17"]
    ["Health", "0.18"]
    ["Computers & Mathematics", "0.93"]
    ["Computers & Mathematics", "0.27"]
    ["Biology & Life Science", "0.85"]
    ["Education", "0.56"]
    ["Social Science", "0.63"]
    ["Business", "0.42"]
    ["Engineering", "0.32"]
    ["Business", "0.28"]
    ["Health", "0.45"]
    ["Biology & Life Science", "0.08"]
    ["Business", "0.20"]
    ["Agriculture & Natural Resources", "0.59"]
    ["Agriculture & Natural Resources", "0.52"]
    ["Engineering", "0.00"]
    ["Engineering", "0.38"]
    ["Biology & Life Science", "0.64"]
    ["Social Science", "0.54"]
    ["Humanities & Liberal Arts", "0.63"]
    ["Psychology & Social Work", "0.44"]
    ["Agriculture & Natural Resources", "0.59"]
    ["Physical Sciences", "0.43"]
    ["Industrial Arts & Consumer Services", "0.43"]
    ["Physical Sciences", "0.51"]
    ["Industrial Arts & Consumer Services", "0.23"]
    ["Business", "0.58"]
    ["Business", "0.38"]
    ["Social Science", "0.49"]
    ["Social Science", "0.47"]
    ["Biology & Life Science", "0.62"]
    ["Computers & Mathematics", "0.18"]
    ["Biology & Life Science", "0.52"]
    ["Biology & Life Science", "0.53"]
    ["Computers & Mathematics", "0.31"]
    ["Physical Sciences", "0.47"]
    ["Business", "0.67"]
    ["Law & Public Policy", "0.59"]
    ["Health", "0.70"]
    ["Law & Public Policy", "0.48"]
    ["Physical Sciences", "0.88"]
    ["Psychology & Social Work", "0.75"]
    ["Biology & Life Science", "0.58"]
    ["Communications & Journalism", "0.31"]
    ["Law & Public Policy", "0.13"]
    ["Arts", "0.37"]
    ["Communications & Journalism", "0.72"]
    ["Physical Sciences", "0.67"]
    ["Communications & Journalism", "0.67"]
    ["Humanities & Liberal Arts", "0.76"]
    ["Education", "0.37"]
    ["Biology & Life Science", "0.91"]
    ["Social Science", "0.62"]
    ["Health", "0.64"]
    ["Health", "0.77"]
    ["Computers & Mathematics", "0.86"]
    ["Industrial Arts & Consumer Services", "0.32"]
    ["Agriculture & Natural Resources", "0.56"]
    ["Biology & Life Science", "0.48"]
    ["Interdisciplinary", "0.50"]
    ["Physical Sciences", "0.12"]
    ["Agriculture & Natural Resources", "0.69"]
    ["Agriculture & Natural Resources", "0.76"]
    ["Education", "0.81"]
    ["Humanities & Liberal Arts", "0.65"]
    ["Humanities & Liberal Arts", "0.73"]
    ["Humanities & Liberal Arts", "0.51"]
    ["Education", "0.73"]
    ["Health", "0.65"]
    ["Education", "0.79"]
    ["Education", "0.45"]
    ["Health", "0.56"]
    ["Biology & Life Science", "0.57"]
    ["Biology & Life Science", "0.60"]
    ["Social Science", "0.53"]
    ["Communications & Journalism", "0.88"]
    ["Health", "0.64"]
    ["Business", "0.73"]
    ["Education", "0.58"]
    ["Humanities & Liberal Arts", "0.76"]
    ["Education", "0.72"]
    ["Social Science", "0.72"]
    ["Biology & Life Science", "0.65"]
    ["Education", "0.60"]
    ["Health", "0.77"]
    ["Humanities & Liberal Arts", "0.42"]
    ["Education", "0.69"]
    ["Humanities & Liberal Arts", "0.34"]
    ["Education", "0.92"]
    ["Industrial Arts & Consumer Services", "0.68"]
    ["Humanities & Liberal Arts", "0.70"]
    ["Arts", "0.69"]
    ["Social Science", "0.50"]
    ["Agriculture & Natural Resources", "0.61"]
    ["Education", "0.42"]
    ["Psychology & Social Work", "0.78"]
    ["Arts", "0.44"]
    ["Education", "0.51"]
    ["Humanities & Liberal Arts", "0.85"]
    ["Arts", "0.67"]
    ["Industrial Arts & Consumer Services", "0.75"]
    ["Psychology & Social Work", "0.81"]
    ["Agriculture & Natural Resources", "0.91"]
    ["Arts", "0.70"]
    ["Education", "0.80"]
    ["Psychology & Social Work", "0.91"]
    ["Psychology & Social Work", "0.90"]
    ["Humanities & Liberal Arts", "0.75"]
    ["Humanities & Liberal Arts", "0.73"]
    ["Arts", "0.58"]
    ["Industrial Arts & Consumer Services", "0.38"]
    ["Agriculture & Natural Resources", "0.72"]
    ["Humanities & Liberal Arts", "0.97"]
    ["Health", "0.71"]
    ["Education", "0.97"]
    ["Humanities & Liberal Arts", "0.69"]
    ["Arts", "0.63"]
    ["Humanities & Liberal Arts", "0.67"]
    ["Biology & Life Science", "0.64"]
    ["Psychology & Social Work", "0.82"]
    ["Psychology & Social Work", "0.80"]
    ["Psychology & Social Work", "0.80"]
    ["Education", "0.88"]


### 大学で仕事を得た人の専攻カテゴリごとの平均と、専攻カテゴリごとの平均人数を割ることで、”大学での仕事の人/全人数の平均”を計算し、30%以下のデータを表示する


```python
#SQL
%sql SELECT Major_category, ROUND(AVG(College_jobs) / AVG(Total), 3) AS share_degree_jobs \
FROM recent_grads \
GROUP BY Major_category HAVING share_degree_jobs < .3;
```

<table>
    <tr>
        <th>Major_category</th>
        <th>share_degree_jobs</th>
    </tr>
    <tr>
        <td>Agriculture &amp; Natural Resources</td>
        <td>0.248</td>
    </tr>
    <tr>
        <td>Arts</td>
        <td>0.265</td>
    </tr>
    <tr>
        <td>Business</td>
        <td>0.114</td>
    </tr>
    <tr>
        <td>Communications &amp; Journalism</td>
        <td>0.22</td>
    </tr>
    <tr>
        <td>Humanities &amp; Liberal Arts</td>
        <td>0.27</td>
    </tr>
    <tr>
        <td>Industrial Arts &amp; Consumer Services</td>
        <td>0.249</td>
    </tr>
    <tr>
        <td>Law &amp; Public Policy</td>
        <td>0.163</td>
    </tr>
    <tr>
        <td>Social Science</td>
        <td>0.215</td>
    </tr>
</table>


```python
# pandas
df_having = df[ ["Major_category", "College_jobs", "Total"] ].groupby( ["Major_category"]).mean()
df_having[ "College_jobs/Total" ] = df_having.apply(lambda x: float("%.3f"%(x[0]/x[1])),axis=1)
df_having = df_having[ df_having["College_jobs/Total"] < 0.3 ]
df_having
```

<div>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>College_jobs</th>
      <th>Total</th>
      <th>College_jobs/Total</th>
    </tr>
    <tr>
      <th>Major_category</th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>Agriculture &amp; Natural Resources</th>
      <td>1986.000000</td>
      <td>7998.100000</td>
      <td>0.248</td>
    </tr>
    <tr>
      <th>Arts</th>
      <td>11848.125000</td>
      <td>44641.250000</td>
      <td>0.265</td>
    </tr>
    <tr>
      <th>Business</th>
      <td>11426.000000</td>
      <td>100182.769231</td>
      <td>0.114</td>
    </tr>
    <tr>
      <th>Communications &amp; Journalism</th>
      <td>21639.000000</td>
      <td>98150.250000</td>
      <td>0.220</td>
    </tr>
    <tr>
      <th>Humanities &amp; Liberal Arts</th>
      <td>12843.333333</td>
      <td>47564.533333</td>
      <td>0.270</td>
    </tr>
    <tr>
      <th>Industrial Arts &amp; Consumer Services</th>
      <td>8171.428571</td>
      <td>32827.428571</td>
      <td>0.249</td>
    </tr>
    <tr>
      <th>Law &amp; Public Policy</th>
      <td>5844.200000</td>
      <td>35821.400000</td>
      <td>0.163</td>
    </tr>
    <tr>
      <th>Social Science</th>
      <td>12662.222222</td>
      <td>58885.111111</td>
      <td>0.215</td>
    </tr>
  </tbody>
</table>
</div>

```ruby
%%ruby
require 'set'
require "sqlite3"
db = SQLite3::Database.new "/var/jobs.db"
cols = db.execute("PRAGMA table_info(recent_grads)").map { |xs| xs[1]}
db.execute( "SELECT * FROM recent_grads ;" ).map {  |xs| cols.zip(xs).to_h
}.map { |xs| ["Major_category", "College_jobs", "Total"].map {|x| xs[x]} 
}.group_by { |xs|  xs[0]}.to_a
.map { |xs| 
    key,vals = xs
    cljs = vals.map { |x| x[1]}
    cljs_mean = cljs.reduce{ |y,x| y+x}/cljs.size 
    totals = vals.map { |x| x[2]}
    totals_mean = totals.reduce{ |y,x| y+x}/totals.size
    [key, cljs_mean.to_f/totals_mean]
}.select{ |xs| xs[1] < 0.3
}.map { |xs|
    key = xs[0]
    val = sprintf("%0.3f", xs[1])
    p [key, val]
}
```

    ["Business", "0.114"]
    ["Law & Public Policy", "0.163"]
    ["Agriculture & Natural Resources", "0.248"]
    ["Industrial Arts & Consumer Services", "0.249"]
    ["Arts", "0.265"]
    ["Social Science", "0.215"]
    ["Humanities & Liberal Arts", "0.270"]
    ["Communications & Journalism", "0.220"]


## まとめ
このように、Pandas, 関数型での処理は全て、基本的なSQLでできることはできるということができそうだとわかりました  
機械学習の文脈は、この中では含んでいませんが、必要なデータを様々な方法で集めて、機械学習のかけるなどはよくするので、前処理の一環でもあります　　

いろんな案件に応じて、適切な集計方法を選択するのですが、SQLに入っていっていて、SQLクエリだけで済むのであれば、そのようにすればいいですし、一台のローカルマシンで済むぐらいのExcelファイルならば、Pandasなどが良いでしょう。複数台数に跨って収められているビッグデータに関しては、MapReduceなどを選択すれば良いでしょう。  

アドテクはこの程度で済むという経験則があるのですが、これ以上何か集計ツールが増えるには、できるだけ機能やシンタックスを対応させて覚えさせて、情報量があまり増えすぎないようにコントロールしたいです、
