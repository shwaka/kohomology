import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { renderHook, act } from "@testing-library/react-hooks"
import React from "react"
import { useDGAEditorDialog } from "."
import { sphere } from "./examples"

test("useDGAEditorDialog", async () => {
  const json = sphere(2)
  const updateDgaWrapper = (json: string): void => { console.log(json) }
  const { result } = renderHook(() => useDGAEditorDialog(json, updateDgaWrapper))
  const TabDialog = result.current.TabDialog
  const { rerender } = render(<TabDialog {...result.current.tabDialogProps}/>)
  act(() => {
    result.current.openDialog()
  })
  rerender(<TabDialog {...result.current.tabDialogProps}/>)
  const dialog = await screen.findByRole("dialog")
  expect(dialog).toContainHTML("Array")
  expect(dialog).toContainHTML("generator")
  const dyInput = within(dialog).getByLabelText("d(y)")
  expect(dyInput).toHaveValue("x^2")
  const dyInput2 = within(dialog).getByDisplayValue("x^2")
  expect(dyInput2).toHaveValue("x^2")
})
