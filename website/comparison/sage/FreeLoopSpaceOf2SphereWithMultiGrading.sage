import sys

if len(sys.argv) == 1:
    print("Input the degree to compute the cohomology")
degree = int(sys.argv[1])

# free loop space of sphere
sphereDim = 2
A.<sx,x,sy,y> = GradedCommutativeAlgebra(
    QQ,
    degrees=(
        (sphereDim, -1),
        (sphereDim, 0),
        (2*sphereDim-1, -1),
        (2*sphereDim-1, 0)
    )
)
B = A.cdg_algebra({y: x^2, sy: -2*x*sx})

total_dim = 0
for n in range(degree):
    total_dim += B.cohomology(n, total=True).dimension()
print(f"Total dim until {degree} is {total_dim}")
