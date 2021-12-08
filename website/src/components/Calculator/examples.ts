export function sphere(dim: number): string {
  if (!Number.isInteger(dim)) {
    throw new Error("dim must be an integer")
  }
  if (dim <= 0) {
    throw new Error("dim must be positive")
  }
  if (dim % 2 == 0) {
    return `[
  ["x", ${dim}, "zero"],
  ["y", ${2*dim - 1}, "x^2"]
]`
  } else {
    return `[
  ["x", ${dim}, "zero"]
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
  ["c", 2, "zero"],
  ["x", ${2*n + 1}, "c^${n + 1}"]
]`
}

export function sevenManifold(): string {
  return `[
  ["a", 2, "zero"],
  ["b", 2, "zero"],
  ["x", 3, "a^2"],
  ["y", 3, "a*b"],
  ["z", 3, "b^2"]
]`
}
