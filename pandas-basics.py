import pandas as pd
import numpy as np
import dask 
import dask.dataframe as dd
# seriesを読み込み
# defaultでは数値はfloat64型になる
ps = pd.Series([1,3,5,np.nan,6,8])
print( ps )

#  これもできない
try:
  ds = dask.Series([1,3,5,np.nan,6,8]) 
except:
  ...

# date rangeを一気に作り出す
dates = pd.date_range('20170101', periods=3)
for date in dates:
  print( date )

# daskにはこういうインターフェースはない
try:
  dask.date_range('20170101', periods=60) 
except Exception as e:
  ...

# pandasで使える便利な非構造化データ
df2 = pd.DataFrame({ 'A' : 1.,
                     'B' : pd.Timestamp('20130102'), # 一回しか、出ないものは全て同じということ
                     'C' : pd.Series(1,index=list(range(4)),dtype='float32'),
                     'D' : np.array([3] * 4,dtype='int32'),
                     'E' : pd.Categorical(["test","train","test","train"]),
                     'F' : 'foo' }) # これも
print( df2 )

# daskで使える便利な非構造化データ
# これは、pandasのデータ構造から、変換する例
df3 = pd.DataFrame({'a':[1,2,3],'b':[4,5,6]})
sd = dd.from_pandas(df3, npartitions=3)
print (sd)
