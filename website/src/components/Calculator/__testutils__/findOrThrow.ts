export function findOrThrow<T>(
  arr: T[],
  predicate: (value: T, index: number, obj: T[]) => boolean,
): T {
  const result = arr.find(predicate)
  const errorMessage = `Item not found from ${arr}`
  if (result === undefined) {
    throw new Error(errorMessage)
  }
  return result
}
