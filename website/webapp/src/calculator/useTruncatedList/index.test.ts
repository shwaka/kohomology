import { renderHook, act } from "@testing-library/react"

import { useTruncatedList } from "./"

describe("useTruncatedList", () => {
  for (const [itemCount, minCount, step] of [[6, 2, 2], [6, 3, 2], [9, 6, 2]]) {
    const items = [...Array(itemCount).keys()].map((n) => `x${n}`)
    const options = { minCount, step }
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
