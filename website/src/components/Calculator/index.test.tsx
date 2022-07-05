import { findByRole, fireEvent, getByRole, getByTestId, getByText, render, screen } from "@testing-library/react"
import React from "react"
import { Calculator } from "."

function getResultsDiv(): HTMLElement {
  return screen.getByTestId("calculator-results")
}

function expectInitialState(): void {
  const results = getResultsDiv()
  expect(results).toContainHTML("Computation results will be shown here")
  expect(results).not.toContainHTML("Cohomology of ")
}

async function clickComputeCohomologyButton(): Promise<void> {
  const computeCohomologyForm = screen.getByTestId("ComputeCohomologyForm")
  expect(computeCohomologyForm).toContainHTML("Compute cohomology")
  const computeCohomologyButton = await findByRole(computeCohomologyForm, "button")
  expect(computeCohomologyButton).toContainHTML("Compute")
  fireEvent.click(computeCohomologyButton)
}

test("Calculator", async () => {
  render(<Calculator/>)
  expectInitialState()
  // const calculator = screen.getByTestId("Calculator")
  const results = getResultsDiv()
  await clickComputeCohomologyButton()
  expect(results).toContainHTML("Cohomology of (Λ(x, y), d) is")
  expect(results).toContainHTML("H^{0} =\\ \\mathbb{Q}\\{[1]\\}")
  expect(results).toContainHTML("H^{2} =\\ \\mathbb{Q}\\{[x]\\}")
})

test("input json", () => {
  render(<Calculator/>)
  expectInitialState()
  // const calculator = screen.getByTestId("Calculator")
  // const results = getResultsDiv()
  const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
  const editDGAButton = getByText(calculatorFormStackItemDGA, "Edit DGA")
  // TODO: この時点では "JsonEditorDialog-input-json" が見つからないことをチェックする
  // expect(screen.getAllByTestId("JsonEditorDialog-input-json").length).toBe(0)
  fireEvent.click(editDGAButton)
  const dialog = screen.getByRole("dialog")
  const jsonTextField = getByTestId(dialog, "JsonEditorDialog-input-json")
  const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
  fireEvent.change(jsonTextField, { target: json })
  const applyButton = getByText(dialog, "Apply")
  fireEvent.click(applyButton)
  clickComputeCohomologyButton()
})
