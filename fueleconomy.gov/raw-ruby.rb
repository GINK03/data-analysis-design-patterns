require 'csv'
require 'ostruct'

keys, *scores = CSV.read('vehicles.csv')
p keys

df = scores.map { |x| 
  r = Hash[ *keys.zip(x).flatten ] 
  r
}

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


