import pandas as pd
import numpy as np

df = pd.read_csv('./vehicles.csv')

# make作成したメーカの数でソート
df1 = df.filter(items=['make', 'year'])
mgroup = df1.groupby(['make'])
#['year'].apply(lambda x:list(x))
df2 = pd.DataFrame( [ [m, len(group['year'].tolist())] for m,group in mgroup] )
df2.columns = ['make', 'freq']
print( df2.sort_values('freq', ascending=False).head() )

# 車の平均の燃費（/年）
df3 = df.filter(items=['make', 'fuelCost08'])
mgroup = df3.groupby(['make'])
df4 = pd.DataFrame( [ [m, group['fuelCost08'].mean() ] for m,group in mgroup] )
df4.columns = ['make', 'fuelCost08']
print( df4.sort_values('fuelCost08', ascending=True).head() )

# 各メーカで燃費が良い車種トップ5
df5 = df.filter(items=['make', 'model', 'fuelCost08'])
mgroup = df5.groupby(['make'])

for make, group in mgroup:
  print(make)
  arr = list(set( [(data[1], data[2]) for index, data in group.iterrows()] ))
  arr = pd.DataFrame(arr)
  arr.columns = ['model', 'fuelCost08']
  print( arr.sort_values('fuelCost08').head() )

# 各年に何台、atvtype（新生代燃料）の車が発表されたか
df6 = df.filter(items=['atvType', 'year'])
# atvTypeがDieaselと　’’をスキップ
df6 = df6[ df6.apply(lambda x: x['atvType'] not in [np.nan, 'Diesel', ''], axis=1) ]
print( df6.head() )

mgroup = df6.groupby(['year'])
df7 = pd.DataFrame( [ [m, len(group['atvType'].tolist())] for m,group in mgroup] )
df7.columns = ['year', 'freq']
print( df7.sort_values(['year'], ascending=False).head(20) )
