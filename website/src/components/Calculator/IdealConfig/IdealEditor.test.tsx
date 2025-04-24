import { render, renderHook, act, screen } from "@testing-library/react"
import React from "react"
import { IdealEditor } from "./IdealEditor"
import { useIdealEditor, UseIdealEditorArgs } from "./useIdealEditor"

describe("IdealEditor", () => {
  test("empty text as generator", async () => {
    const hookArgs: UseIdealEditorArgs = {
      idealJson: "[]",
      setIdealJson: jest.fn(),
      validateGenerator: jest.fn().mockResolvedValue(true),
      validateGeneratorArray: jest.fn().mockResolvedValue(true),
    }
    const { result } = renderHook(() => useIdealEditor(hookArgs))
    const { rerender } = render(<IdealEditor {...result.current.idealEditorProps}/>)

    act(() => {
      result.current.idealEditorProps.append({ text: "" })
    })
    rerender(<IdealEditor {...result.current.idealEditorProps}/>)
    await act(async () => {
      await result.current.getOnSubmit(jest.fn())()
    })
    rerender(<IdealEditor {...result.current.idealEditorProps}/>)

    expect(screen.getByRole("alert")).toContainHTML("Please enter the generator.")
  })
})
