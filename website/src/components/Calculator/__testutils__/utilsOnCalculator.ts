import { fireEvent, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
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

export async function clickRestartButton(): Promise<void> {
  // Open dialog
  const buttons = screen.getAllByRole("button")
  const restartButton = buttons.find((element) => (
    (element !== null) && (element.textContent === "Restart")
  ))
  fireEvent.click(restartButton)

  // Close dialog
  const dialogs = screen.getAllByRole("dialog")
  const dialog = dialogs.find((element) => (
    (element !== null) && (element.innerHTML.includes("Are you sure to restart"))
  ))
  const buttonsInDialog = within(dialog).getAllByRole("button")
  const restartButtonInDialog = buttonsInDialog.find((element) => (
    (element !== null) && (element.textContent === "Restart")
  ))
  fireEvent.click(restartButtonInDialog)
  await waitForElementToBeRemoved(dialog) // It takes some time to remove the dialog.
}

function isRadioGroupForTargets(element: Element | null): boolean {
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
  const radiogroups = screen.getAllByRole("radiogroup")
  const radiogroup = radiogroups.find(isRadioGroupForTargets)
  const radios = within(radiogroup).getAllByRole("radio")
  const input = radios.find((element) => (
    (element !== null) && (element.outerHTML.includes(`value="${targetName}"`))
  ))
  fireEvent.click(input)
}

export function expectSnackbarToContainHTML(htmlToBeContained: string[]): void {
  const snackbar = screen.getByRole("presentation")
  for (const html of htmlToBeContained) {
    expect(snackbar).toContainHTML(html)
  }
}
