import { prettifyDGAJson } from "@components/Calculator/jsonUtils"
import { renderHook, act } from "@testing-library/react"
import { useArrayEditor } from "./useArrayEditor"

const json = JSON.stringify([
  ["x", 2, "0"],
  ["y", 3, "x^2"]
])

describe("useArrayEditor", () => {
  it("should initialize with given json", () => {
    const { result } = renderHook(() =>
      useArrayEditor({ json, updateDgaWrapper: jest.fn() })
    )

    const values = result.current.arrayEditorPropsExceptOnSubmit.getValues()
    expect(values.generatorArray).toHaveLength(2)
    expect(values.generatorArray[0].name).toBe("x")
    expect(values.generatorArray[0].degree).toBe(2)
    expect(values.generatorArray[0].differentialValue).toBe("0")
    expect(values.generatorArray[1].name).toBe("y")
    expect(values.generatorArray[1].degree).toBe(3)
    expect(values.generatorArray[1].differentialValue).toBe("x^2")
  })

  it("should append a new generator", () => {
    const { result } = renderHook(() =>
      useArrayEditor({ json, updateDgaWrapper: jest.fn() })
    )

    act(() => {
      result.current.arrayEditorPropsExceptOnSubmit.append({
        name: "z",
        degree: 1,
        differentialValue: "0"
      })
    })

    const values = result.current.arrayEditorPropsExceptOnSubmit.getValues()
    expect(values.generatorArray).toHaveLength(3)
    expect(values.generatorArray[2].name).toBe("z")
    expect(values.generatorArray[2].degree).toBe(1)
    expect(values.generatorArray[2].differentialValue).toBe("0")
  })

  it("should call updateDgaWrapper on submit", async () => {
    const update = jest.fn()
    const { result } = renderHook(() =>
      useArrayEditor({ json, updateDgaWrapper: update })
    )

    await act(async () => {
      await result.current.editorWithoutRender.getOnSubmit(jest.fn())() // dummy closeDialog
    })

    expect(update).toHaveBeenCalledWith(expect.stringContaining('["x", 2, "0"]'))
  })

  it("should detect unsaved changes in preventQuit", () => {
    // json will be compared to prettified one
    const prettyJson = prettifyDGAJson(json)
    const { result } = renderHook(() =>
      useArrayEditor({ json: prettyJson, updateDgaWrapper: jest.fn() })
    )

    expect(result.current.editorWithoutRender.preventQuit).not.toBeUndefined()
    const preventQuit = result.current.editorWithoutRender.preventQuit as () => string | undefined

    // preventQuit should return undefined if unchanged
    expect(preventQuit()).toBeUndefined()

    act(() => {
      result.current.arrayEditorPropsExceptOnSubmit.append({
        name: "z",
        degree: 1,
        differentialValue: "0"
      })
    })

    // preventQuit should return non-undefined value
    expect(preventQuit()).toContain("Your input is not saved. Are you sure you want to quit?")
  })
})
