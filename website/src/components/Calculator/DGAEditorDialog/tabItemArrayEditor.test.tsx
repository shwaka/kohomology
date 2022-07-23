import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { renderHook, act } from "@testing-library/react-hooks"
import React from "react"
import { sphere } from "./examples"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"

test("useTabItemArrayEditor", async () => {
  const json = sphere(2)
  const updateDgaWrapper = (json: string): void => { console.log(json) }
  const { result } = renderHook(() => useTabItemArrayEditor({ json, updateDgaWrapper }))
  const { rerender, container } = render(result.current.render())
  // check value of input elements
  const differentialValueInputs = screen.getAllByTestId("ArrayEditor-input-differentialValue")
  expect(differentialValueInputs[0]).toHaveValue("0")
  expect(differentialValueInputs[1]).toHaveValue("x^2")
  // input invalid value
  fireEvent.input(differentialValueInputs[1], { target: { value: "x" } })
  const closeDialog = () => {}
  await act(async () => {
    result.current.onSubmit(closeDialog)
  })
  rerender(result.current.render())
  const alert = screen.getByRole("alert")
  expect(alert).toContainHTML("The degree of d(y) is expected to be deg(y)+1=4, but the given value x has degree 2.")
})
