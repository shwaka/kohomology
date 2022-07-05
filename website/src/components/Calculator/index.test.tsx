import { findByRole, fireEvent, getByRole, getByTestId, getByText, render, screen, waitFor } from "@testing-library/react"
import React from "react"
import { Calculator } from "."

function getResultsDiv(): HTMLElement {
  return screen.getByTestId("calculator-results")
}

function expectResultsToContainHTML(htmlToBeContained: string[], htmlNotToBeContained: string[] = []): void {
  const resultsDiv = getResultsDiv()
  for (const html of htmlToBeContained) {
    expect(resultsDiv).toContainHTML(html)
  }
  for (const html of htmlNotToBeContained) {
    expect(resultsDiv).not.toContainHTML(html)
  }
}

function expectInitialState(): void {
  expectResultsToContainHTML(
    ["Computation results will be shown here"],
    ["Cohomology of "],
  )
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
  await clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Cohomology of (Λ(x, y), d) is",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{2} =\\ \\mathbb{Q}\\{[x]\\}"
    ],
  )
})

test("input json", async () => {
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
  fireEvent.input(jsonTextField, { target: { value: json } })
  const applyButton = getByText(dialog, "Apply")
  fireEvent.click(applyButton)
  await clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Cohomology of (Λ(x, y, z), d) is",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
})
