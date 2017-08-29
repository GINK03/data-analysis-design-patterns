

xs = (0..20).map { |x| x }
# 2で割り切れる
arr = xs.select { |x| x%2 == 0 }.map { |x| x**2 }
p arr
