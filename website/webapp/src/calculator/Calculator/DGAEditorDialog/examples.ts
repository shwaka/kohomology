export function sphere(dim: number): string {
  if (!Number.isInteger(dim)) {
    throw new Error("dim must be an integer")
  }
  if (dim <= 0) {
    throw new Error("dim must be positive")
  }
  if (dim % 2 == 0) {
    return `[
  ["x", ${dim}, "0"],
  ["y", ${2 * dim - 1}, "x^2"]
]`
  } else {
    return `[
  ["x", ${dim}, "0"]
]`
  }
}

export function complexProjective(n: number): string {
  if (!Number.isInteger(n)) {
    throw new Error("dim must be an integer")
  }
  if (n <= 0) {
    throw new Error("dim must be positive")
  }
  return `[
  ["c", 2, "0"],
  ["x", ${2 * n + 1}, "c^${n + 1}"]
]`
}

export function sevenManifold(): string {
  return `[
  ["a", 2, "0"],
  ["b", 2, "0"],
  ["x", 3, "a^2"],
  ["y", 3, "a*b"],
  ["z", 3, "b^2"]
]`
}

export function arkowitzLupton(): string {
  return `[
  ["x_1", 10, "0"],
  ["x_2", 12, "0"],
  ["y_1", 41, "x_1^3 * x_2"],
  ["y_2", 43, "x_1^2 * x_2^2"],
  ["y_3", 45, "x_1 * x_2^3"],
  ["z", 119, "x_2 * (y_1*x_2 - x_1*y_2) * (y_2*x_2 - x_1*y_3) + x_1^12 + x_2^10"]
]`
}
