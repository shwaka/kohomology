import { renderHook, act } from "@testing-library/react"

import { Indeterminate, IndeterminateFormInput } from "./__playground__/schema"
import { useIndeterminateArrayEditorProps } from "./__playground__/useIndeterminateArrayEditorProps"

const defaultArray: Indeterminate[] = [
  {
    name: "x",
    degree: 2,
  },
  {
    name: "y",
    degree: 3,
  },
]
const defaultValues: IndeterminateFormInput = {
  indeterminateArray: defaultArray,
}

describe("useArrayEditorProps", () => {
  it("should initialize with the default values", () => {
    const { result } = renderHook(() =>
      useIndeterminateArrayEditorProps({ defaultValues, setValues: jest.fn() })
    )

    const values = result.current.arrayEditorPropsPartial.getValues()
    expect(values.indeterminateArray).toHaveLength(2)
    expect(values.indeterminateArray[0].name).toBe("x")
    expect(values.indeterminateArray[0].degree).toBe(2)
    expect(values.indeterminateArray[1].name).toBe("y")
    expect(values.indeterminateArray[1].degree).toBe(3)
  })

  it("should append a new value", () => {
    const { result } = renderHook(() =>
      useIndeterminateArrayEditorProps({ defaultValues, setValues: jest.fn() })
    )

    act(() => {
      result.current.arrayEditorPropsPartial.append({
        name: "z",
        degree: 5,
      })
    })

    const values = result.current.arrayEditorPropsPartial.getValues()
    expect(values.indeterminateArray).toHaveLength(3)
    expect(values.indeterminateArray[2].name).toBe("z")
    expect(values.indeterminateArray[2].degree).toBe(5)
  })

  it("should call setValues on submit", async () => {
    const setValues = jest.fn()
    const { result } = renderHook(() =>
      useIndeterminateArrayEditorProps({ defaultValues, setValues })
    )

    await act(async () => {
      const closeDialog = jest.fn()
      const onSubmit = result.current.editorWithoutRender.getOnSubmit(closeDialog)
      await onSubmit()
    })

    expect(setValues).toHaveBeenCalledOnceWith(defaultValues)
  })

  it("should detect unsaved changes in preventQuit", () => {
    const { result } = renderHook(() =>
      useIndeterminateArrayEditorProps({ defaultValues, setValues: jest.fn() })
    )

    expect(result.current.editorWithoutRender.preventQuit).not.toBeUndefined()
    const preventQuit = result.current.editorWithoutRender.preventQuit as () => string | undefined

    // preventQuit should return undefined if unchanged
    expect(preventQuit()).toBeUndefined()

    act(() => {
      result.current.arrayEditorPropsPartial.append({
        name: "z",
        degree: 1,
      })
    })

    // preventQuit should return non-undefined value
    expect(preventQuit()).toContain("Your input is not saved. Are you sure you want to quit?")
  })
})
