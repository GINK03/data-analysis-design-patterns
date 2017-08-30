
File.readlines('911report.txt').map { |x|
  x.split(' ').map { |x| x }
}.flatten.group_by { |x| 
  x 
}.to_a.map { |x| 
  key, arr = x
  [key, arr.size] 
}.sort_by { |x|
  x[1]*-1
}.slice(0..20).map { |x| 
  p x 
}
