from pandas import DataFrame


raw = [i for i in range(20)]
df = DataFrame( { '1' : raw } )
df = df[lambda x:x['1'] % 2 == 0 ]
df = df.apply(lambda x:x**2)
print( df.head(20).values.tolist() )
