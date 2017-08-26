import pandas as pd
url = 'https://raw.githubusercontent.com/justmarkham/DAT8/master/data/chipotle.tsv'
chipo = pd.read_csv(url, sep = '\t')

# top 10を取り出し
print( chipo.head() )

# info
# 行の情報を確認することができる
print( chipo.info() )

# データの形　
# 列、行ででる
print( chipo.shape )

# 行の確認
print( chipo.columns )

# 列の確認
print( chipo.index )

# 最も頻出するカテゴリの確認
print( chipo.item_name.value_counts().head() )

# 合計値の計算
total_items_orders = chipo.quantity.sum()
print( total_items_orders )

# dollerize(cent -> doll)
# lambda式でのワンライン加工
dollarizer = lambda x: float(x[1:-1])
chipo.item_price = chipo.item_price.apply(dollarizer)
print( chipo.item_price.head() )


# 値の数え上げ
print( chipo.order_id.value_counts().count() )

# groupby
order_grouped = chipo.groupby(by=['order_id']).sum()
print( order_grouped.mean()['item_price'] )
