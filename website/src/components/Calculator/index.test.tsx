import { useLocation } from "@docusaurus/router"
import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
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
  const computeCohomologyButton = within(computeCohomologyForm).getByRole("button")
  expect(computeCohomologyButton).toContainHTML("Compute")
  fireEvent.click(computeCohomologyButton)
}

class InputJson {
  private static openDialog(): HTMLElement {
    // Open dialog
    const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
    const editDGAButton = within(calculatorFormStackItemDGA).getByText("Edit DGA")
    expect(screen.queryByTestId("JsonEditorDialog-input-json")).not.toBeInTheDocument()
    fireEvent.click(editDGAButton)
    const dialog = screen.getByRole("dialog")
    // Select the "JSON" tab
    const jsonTabButton = within(dialog).getByRole((role, element) => (
      role === "tab" && element?.textContent?.includes("JSON")
    ))
    fireEvent.click(jsonTabButton)
    return dialog
  }

  private static inputAndApplyJson(dialog: HTMLElement, json: string): void {
    // Input json
    const jsonTextField = within(dialog).getByTestId("JsonEditorDialog-input-json")
    fireEvent.input(jsonTextField, { target: { value: json } })
    // Click "Apply" button
    const applyButton = within(dialog).getByText("Apply")
    fireEvent.click(applyButton)
  }

  static async inputValidJson(json: string): Promise<void> {
    const dialog = InputJson.openDialog()
    InputJson.inputAndApplyJson(dialog, json)
    await waitForElementToBeRemoved(dialog) // It takes some time to remove the dialog.
  }

  static async inputInvalidJson(json: string): Promise<void> {
    const dialog = InputJson.openDialog()
    InputJson.inputAndApplyJson(dialog, json)
    await within(dialog).findByRole("alert") // It takes some time to show alert.
  }
}

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

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
  await InputJson.inputValidJson(json)
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Cohomology of (Λ(x, y, z), d) is",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
})

test("invalid json", async () => {
  render(<Calculator/>)
  expectInitialState()
  const json = "invalid json"
  await InputJson.inputInvalidJson(json)
  const dialog = screen.getByRole("dialog")
  expect(dialog).toContainHTML("Unexpected JSON token at offset 0")
  expect(dialog).toContainHTML(`JSON input: ${json}`)
})

test("url query", async () => {
  mockUseLocation.mockReturnValue({
    search: "?dgaJson=%5B%5B%22x%22%2C3%2C%22zero%22%5D%2C%5B%22y%22%2C3%2C%22zero%22%5D%2C%5B%22z%22%2C5%2C%22x+*+y%22%5D%5D"
  })
  render(<Calculator/>)
  expectInitialState()
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Cohomology of (Λ(x, y, z), d) is",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
})

test("url query with invalid json", async () => {
  mockUseLocation.mockReturnValue({
    search: "?dgaJson=invalidJson"
  })
  render(<Calculator/>)
  expectInitialState()
  expectResultsToContainHTML(
    [
      "[Error] Invalid JSON is given as URL parameter.",
    ]
  )
})