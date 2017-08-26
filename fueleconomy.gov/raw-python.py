import os
import sys

with open('vehicles.csv') as f:
  keys = next(f).strip().split(',')
  for line in f:
    line = line.strip()
    vals = line.split(',')
    obj = dict(zip(keys,vals))
    print( obj )
