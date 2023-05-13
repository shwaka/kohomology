import { fireEvent, screen, within } from "@testing-library/react"

function getResultsDiv(): HTMLElement {
  return screen.getByTestId("calculator-results")
}

export function expectResultsToContainHTML(htmlToBeContained: string[], htmlNotToBeContained: string[] = []): void {
  const resultsDiv = getResultsDiv()
  for (const html of htmlToBeContained) {
    expect(resultsDiv).toContainHTML(html)
  }
  for (const html of htmlNotToBeContained) {
    expect(resultsDiv).not.toContainHTML(html)
  }
}

export function expectInitialState(): void {
  expectResultsToContainHTML(
    ["Computation results will be shown here"],
    ["Computing "],
  )
}

export function getComputeCohomologyButton(): HTMLElement{
  const computeCohomologyForm = screen.getByTestId("ComputeCohomologyForm")
  expect(computeCohomologyForm).toContainHTML("Compute cohomology")
  const computeCohomologyButton = within(computeCohomologyForm).getByRole("button")
  return computeCohomologyButton
}

export function clickComputeCohomologyButton(): void {
  const computeCohomologyButton = getComputeCohomologyButton()
  expect(computeCohomologyButton).toContainHTML("Compute")
  fireEvent.click(computeCohomologyButton)
}
