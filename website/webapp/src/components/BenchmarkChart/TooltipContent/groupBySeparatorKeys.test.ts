import { range } from "remeda"

import { groupBySeparatorKeys } from "./groupBySeparatorKeys"

describe("groupBySeparatorKeys", () => {
  const k = 10
  function getKey(item: { key: number, value: string }): number {
    return item.key
  }
  function getValue(item: { key: number, value: string }): string {
    return item.value
  }

  for (const n of range(2, 6)) {
    test(`multiples of ${n} as keys`, () => {
      const array = range(0, n * k + 1).map((i) => ({
        key: i,
        value: `[${i}]`,
      }))
      const separatorKeys = range(0, k + 1).map((i) => n * i)
      const groups = groupBySeparatorKeys(array, separatorKeys, getKey)
      expect(groups.get(0)?.map(getValue)).toEqual(["[0]"])
      for (const i of range(1, k + 1)) {
        const expected = range(1, n + 1).map((j) => {
          const value = n * (i - 1) + j
          return `[${value}]`
        })
        expect(groups.get(n * i)?.map(getValue)).toEqual(expected)
      }
    })
  }
})
