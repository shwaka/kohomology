export function groupBySeparatorKeys<T, K>(
  array: T[],
  separatorKeys: K[],
  getKey: (item: T) => K,
): Map<K, T[]> {
  const keySet: Set<K> = new Set(separatorKeys)
  function getNextSeparatorIndex(index: number): number {
    if (index >= array.length) {
      throw new Error(`index=${index} is larger than the length of the array`)
    }
    let current: number = index
    while (true) {
      if (current === array.length - 1) {
        return array.length - 1
      }
      const key = getKey(array[current])
      if (keySet.has(key)) {
        return current
      }
      current++
    }
  }
  const result: Map<K, T[]> = new Map()
  let start: number = 0
  let end: number = 0
  while (start < array.length) {
    end = getNextSeparatorIndex(start)
    const key = getKey(array[end])
    const subArray = array.slice(start, end + 1)
    result.set(key, subArray)
    start = end + 1
  }
  return result
}
