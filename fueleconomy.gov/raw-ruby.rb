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

# 車の平均の燃費
df.map{ |x| 
  d = OpenStruct.new
  d.make = x['make'] 
  d.cost = x['fuelCost08']
  d
}.group_by { |x| 
  x.make
}.map { |xs|
  make, arr = xs
  costs = arr.map { |x| 
    x.cost.to_i
  } 
  amount = costs.reduce { |y,x| 
    y + x
  }
  size   = costs.size
  mean = amount/size
  [make, mean]
}.sort_by { |x| 
  x[1]
}.slice(0..10).map { |a| 
  p a
}

# 各年に何台、atvtype（新生代燃料）の車が発表された
df.map{ |x| 
  d = OpenStruct.new
  d.type = x["atvType"]
  d.year = x["year"]
  d
}.map { |x| 
  if x.type != nil and x.type != 'Diesel' then 
    x.meta = 'eco'
  else
    x.meta = 'noeco'
  end
  x
}.group_by { |x|
  x.meta + ' ' + x.year 
}.map { |xs| 
  year, arr = xs
  print year, ' ', arr.size, "\n"
}
