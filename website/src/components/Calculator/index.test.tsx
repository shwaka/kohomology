import { fireEvent, getByRole, getByTestId, getByText, render, screen, waitForElementToBeRemoved } from "@testing-library/react"
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

function clickComputeCohomologyButton(): void {
  const computeCohomologyForm = screen.getByTestId("ComputeCohomologyForm")
  expect(computeCohomologyForm).toContainHTML("Compute cohomology")
  const computeCohomologyButton = getByRole(computeCohomologyForm, "button")
  expect(computeCohomologyButton).toContainHTML("Compute")
  fireEvent.click(computeCohomologyButton)
}

async function inputJson(json: string): Promise<void> {
  // Open dialog
  const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
  const editDGAButton = getByText(calculatorFormStackItemDGA, "Edit DGA")
  expect(screen.queryByTestId("JsonEditorDialog-input-json")).toBeNull()
  fireEvent.click(editDGAButton)
  // Input json
  const dialog = screen.getByRole("dialog")
  const jsonTextField = getByTestId(dialog, "JsonEditorDialog-input-json")
  fireEvent.input(jsonTextField, { target: { value: json } })
  // Click "Apply" button
  const applyButton = getByText(dialog, "Apply")
  fireEvent.click(applyButton)
  await waitForElementToBeRemoved(dialog) // It takes some time to remove the dialog.
}

test("Calculator", async () => {
  render(<Calculator/>)
  expectInitialState()
  // const calculator = screen.getByTestId("Calculator")
  clickComputeCohomologyButton()
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
  const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
  await inputJson(json)
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Cohomology of (Λ(x, y, z), d) is",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
})
