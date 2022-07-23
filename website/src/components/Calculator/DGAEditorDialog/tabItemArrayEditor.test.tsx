import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { renderHook, act } from "@testing-library/react-hooks"
import React from "react"
import { sphere } from "./examples"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"

test("useTabItemArrayEditor", async () => {
  const json = sphere(2)
  const updateDgaWrapper = (json: string): void => { console.log(json) }
  const { result } = renderHook(() => useTabItemArrayEditor({ json, updateDgaWrapper }))
  const { rerender } = render(result.current.render())
  rerender(result.current.render())
  // check value of input elements
  const differentialValueInputs = screen.getAllByTestId("ArrayEditor-input-differentialValue")
  expect(differentialValueInputs[0]).toHaveValue("0")
  expect(differentialValueInputs[1]).toHaveValue("x^2")
})
