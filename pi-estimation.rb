
rand = Random.new(1234)
MAX = 10000000
size = (0..MAX).map { |x| 
  a,b = rand.rand, rand.rand
  a**2 + b**2
}.select { |x| 
  x < 1.0
}.size
print 'estimate pi ', 4*size.to_f/MAX, "\n"
