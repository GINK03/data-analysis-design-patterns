
import dask.array as da

x = da.fromfunction(lambda i, j, k: i + j + k**2, chunks=(5, 512, 512), shape=(100, 2048, 2048), dtype='f8')
x.to_hdf5('myfile.hdf5', '/x', compression='lzf', shuffle=True)
