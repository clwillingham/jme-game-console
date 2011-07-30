def c = { x, y -> x + y }
x = c.curry(34)
println x(3)
