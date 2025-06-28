
import { EditorDialog } from "@calculator/Editor"
import { render, screen, within, renderHook, act } from "@testing-library/react"

import { useDGAEditorDialog } from "."
import { sphere } from "./examples"

test("useDGAEditorDialog", async () => {
  const json = sphere(2)
  const updateDgaWrapper = (json: string): void => { console.log(json) }
  const { result } = renderHook(() => useDGAEditorDialog(json, updateDgaWrapper))
  const { rerender } = render(<EditorDialog {...result.current.editorDialogProps} />)
  act(() => {
    result.current.openDialog()
  })
  rerender(<EditorDialog {...result.current.editorDialogProps} />)
  // check texts in dialog
  const dialog = await screen.findByRole("dialog")
  expect(dialog).toContainHTML("Array")
  expect(dialog).toContainHTML("generator")
  // check value of input elements
  const differentialValueInputs = within(dialog).getAllByTestId("ArrayEditor-input-differentialValue")
  expect(differentialValueInputs[0]).toHaveValue("0")
  expect(differentialValueInputs[1]).toHaveValue("x^2")
})
