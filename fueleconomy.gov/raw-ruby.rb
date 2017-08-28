require 'csv'
require 'ostruct'
require 'set'

keys, *scores = CSV.read('vehicles.csv')

df = scores.map { |x| 
  r = Hash[ *keys.zip(x).flatten ] 
  r
}

# make作成したメーカの数でソート
df.map{ |x| 
  d = OpenStruct.new
  d.make = x['make'] 
  d.cost = x['fuelCost08']
  d
}.group_by { |x| 
  x.make
}.map { |xs|
  make, arr = xs
  d = OpenStruct.new
  d.make = make
  d.freq = arr.size.to_i
  d
}.sort_by { |x|
  x.freq * -1
}.slice(0..20).map { |x| 
  print x.make, ' ', x.freq, "\n"
}

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
