import { fireEvent, screen, within } from "@testing-library/react"
import { TargetName, targetNames } from "../worker/workerInterface"

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

export function expectComputeCohomologyButtonToContain(text: "Compute" | "Computing"): void {
  const computeCohomologyButton = getComputeCohomologyButton()
  expect(computeCohomologyButton).toContainHTML(text)
}

function isRadioGroupForTargets(role: string, element: Element | null): boolean {
  if (role !== "radiogroup") {
    return false
  }
  if (element === null) {
    return false
  }
  for (const targetName of targetNames) {
    if (!element.innerHTML.includes(`value="${targetName}"`)) {
      return false
    }
  }
  return true
}

export function selectComputationTarget(targetName: TargetName): void {
  const radiogroup = screen.getByRole(isRadioGroupForTargets)
  const input = within(radiogroup).getByRole((role, element) => (
    (role === "radio") && (element !== null) &&
      (element.outerHTML.includes(`value="${targetName}"`))
  ))
  fireEvent.click(input)
}
