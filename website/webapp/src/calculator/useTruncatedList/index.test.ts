import { renderHook, act } from "@testing-library/react"
import * as R from "remeda"

import { useTruncatedList, UseTruncatedListOptions } from "./"

describe("useTruncatedList with getWeight always returning 1", () => {
  for (const [itemCount, minCount, step] of [[6, 2, 2], [6, 3, 2], [9, 6, 2]]) {
    const items = R.range(0, itemCount).map((n) => `x${n}`)
    const options: UseTruncatedListOptions<string> = { minWeight: minCount, step }
    const stepCount = Math.ceil((items.length - minCount) / step)

    const paramInfo = `itemCount=${itemCount}, minCount=${minCount}, step=${step}`

    if (items.length <= minCount + step) {
      throw new Error(
        `items.length must be larger than minCount + step: ${paramInfo}`
      )
    }

    describe(`with ${paramInfo}`, () => {
      it("shows minCount items initially", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        expect(result.current.visibleItems).toEqual(items.slice(0, minCount))
        expect(result.current.visibleCount).toBe(minCount)
        expect(result.current.availableCommands.showMore).toBe(true)
        expect(result.current.availableCommands.showLess).toBe(false)
        expect(result.current.availableCommands.showAll).toBe(true)
        expect(result.current.availableCommands.showMin).toBe(false)
      })

      it("increases visible items by step when showMore is called", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        act(() => result.current.commands.showMore())
        expect(result.current.visibleItems).toEqual(items.slice(0, minCount + step))
        expect(result.current.visibleCount).toBe(minCount + step)
      })

      it("shows all items after repeated showMore calls", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        for (let i = 0; i < stepCount; i++) {
          act(() => result.current.commands.showMore())
        }
        expect(result.current.visibleItems).toEqual(items)
        expect(result.current.visibleCount).toBe(items.length)
        expect(result.current.availableCommands.showMore).toBe(false)
      })

      it("decreases visible items by step when showLess is called", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        act(() => result.current.commands.showAll())
        act(() => result.current.commands.showLess())
        expect(result.current.visibleItems).toEqual(items.slice(0, items.length - step))
        expect(result.current.visibleCount).toBe(items.length - step)
      })

      it("resets to minCount when showMin is called", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        act(() => result.current.commands.showAll())
        act(() => result.current.commands.showMin())
        expect(result.current.visibleItems).toEqual(items.slice(0, minCount))
        expect(result.current.visibleCount).toBe(minCount)
        expect(result.current.availableCommands.showLess).toBe(false)
        expect(result.current.availableCommands.showMin).toBe(false)
      })

      it("shows all items when showAll is called", () => {
        const { result } = renderHook(() => useTruncatedList(items, options))
        act(() => result.current.commands.showAll())
        expect(result.current.visibleItems).toEqual(items)
        expect(result.current.visibleCount).toBe(items.length)
      })
    })
  }
})

describe("useTruncatedList with getWeight = string.length", () => {
  const items = ["abc", "d", "efg", "h", "ijkl"] // 3,1,3,1,4 = total 12
  const getWeight = (s: string): number => s.length

  it("respects minWeight and returns items up to that weight", () => {
    const { result } = renderHook(() =>
      useTruncatedList(items, { minWeight: 4, step: 3, getWeight })
    )

    expect(result.current.visibleItems).toEqual(["abc", "d"]) // 3 + 1 = 4
    expect(result.current.visibleCount).toBe(2)
    expect(result.current.isTruncated).toBe(true)
    expect(result.current.availableCommands.showMore).toBe(true)
    expect(result.current.availableCommands.showLess).toBe(false)
  })

  it("increments visible items by weight step on showMore", () => {
    const { result } = renderHook(() =>
      useTruncatedList(items, { minWeight: 4, step: 3, getWeight })
    )

    act(() => result.current.commands.showMore())
    // weights: "abc"(3) + "d"(1) + "efg"(3) = 7, still within 4 + 3 = 7
    expect(result.current.visibleItems).toEqual(["abc", "d", "efg"])
    expect(result.current.visibleCount).toBe(3)
    expect(result.current.isTruncated).toBe(true)
  })

  it("stops when total weight reached", () => {
    const { result } = renderHook(() =>
      useTruncatedList(items, { minWeight: 4, step: 3, getWeight })
    )

    act(() => result.current.commands.showMore())
    act(() => result.current.commands.showMore()) // total 4 + 3 + 3 = 10
    act(() => result.current.commands.showMore()) // should reach full (12)

    expect(result.current.visibleItems).toEqual(items)
    expect(result.current.visibleCount).toBe(items.length)
    expect(result.current.isTruncated).toBe(false)
    expect(result.current.availableCommands.showMore).toBe(false)
  })

  it("showAll reveals all items regardless of current weight", () => {
    const { result } = renderHook(() =>
      useTruncatedList(items, { minWeight: 4, step: 3, getWeight })
    )

    act(() => result.current.commands.showAll())

    expect(result.current.visibleItems).toEqual(items)
    expect(result.current.visibleCount).toBe(items.length)
    expect(result.current.isTruncated).toBe(false)
  })

  it("showMin brings visible items back to minWeight range", () => {
    const { result } = renderHook(() =>
      useTruncatedList(items, { minWeight: 4, step: 3, getWeight })
    )

    act(() => result.current.commands.showAll())
    act(() => result.current.commands.showMin())

    expect(result.current.visibleItems).toEqual(["abc", "d"])
    expect(result.current.visibleCount).toBe(2)
    expect(result.current.isTruncated).toBe(true)
  })
})
