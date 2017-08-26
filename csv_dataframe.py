import dask.dataframe as dd
df = dd.read_csv('Sacramentorealestatetransactions.csv')
res = df.groupby(df.beds).compute()
print( res )
