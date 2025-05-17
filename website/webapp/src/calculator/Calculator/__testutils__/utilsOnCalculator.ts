import { screen, waitFor, waitForElementToBeRemoved, within } from "@testing-library/react"
import { UserEvent } from "@testing-library/user-event"
import { TargetName, targetNames } from "../worker/workerInterface"
import { findOrThrow } from "./findOrThrow"
import { getStyledMessages } from "./getStyledMessages"

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

export function expectResultsToContainMessages(
  messagesToBeContained: string[],
  messagesNotToBeContained: string[] = [],
): void {
  const results = getStyledMessages().results
  for (const message of messagesToBeContained) {
    expect(results).toContainEqual(expect.stringContaining(message))
  }
  for (const message of messagesNotToBeContained) {
    expect(results).not.toContainEqual(expect.stringContaining(message))
  }
}

export async function waitForInitialState(expectedFormMessage: string | undefined = undefined): Promise<void> {
  await waitFor(() => {
    expectResultsToContainHTML(
      ["Computation results will be shown here"],
      ["Computing "],
    )
    expect(getStyledMessages().form).toContainEqual(
      expectedFormMessage ?? "$(\\Lambda V, d) = $ $(\\Lambda($ $x,\\ $ $y$ $), d)$"
    )
  })
}

export function getComputeCohomologyButton(): HTMLElement{
  const computeCohomologyForm = screen.getByTestId("ComputeCohomologyForm")
  expect(computeCohomologyForm).toContainHTML("Compute cohomology")
  const computeCohomologyButton = within(computeCohomologyForm).getByRole("button")
  return computeCohomologyButton
}

export async function clickComputeCohomologyButton(user: UserEvent): Promise<void> {
  const computeCohomologyButton = getComputeCohomologyButton()
  expect(computeCohomologyButton).toContainHTML("Compute")
  await user.click(computeCohomologyButton)
}

export function expectComputeCohomologyButtonToContain(text: "Compute" | "Computing"): void {
  const computeCohomologyButton = getComputeCohomologyButton()
  expect(computeCohomologyButton).toContainHTML(text)
}

export async function clickRestartButton(user: UserEvent): Promise<void> {
  // Open dialog
  const buttons = screen.getAllByRole("button")
  const restartButton = findOrThrow(buttons, (element) => (
    (element !== null) && (element.textContent === "Restart")
  ))
  await user.click(restartButton)

  // Close dialog
  const dialogs = screen.getAllByRole("dialog")
  const dialog = findOrThrow(dialogs, (element) => (
    (element !== null) && (element.innerHTML.includes("Are you sure to restart"))
  ))
  const buttonsInDialog = within(dialog).getAllByRole("button")
  const restartButtonInDialog = findOrThrow(buttonsInDialog, (element) => (
    (element !== null) && (element.textContent === "Restart")
  ))
  await user.click(restartButtonInDialog)
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

export async function selectComputationTarget(user: UserEvent, targetName: TargetName): Promise<void> {
  const radiogroups = screen.getAllByRole("radiogroup")
  const radiogroup = findOrThrow(radiogroups, isRadioGroupForTargets)
  const radios = within(radiogroup).getAllByRole("radio")
  const input = findOrThrow(radios, (element) => (
    (element !== null) && (element.outerHTML.includes(`value="${targetName}"`))
  ))
  await user.click(input)
}

export function expectSnackbarToContainHTML(htmlToBeContained: string[]): void {
  const snackbar = screen.getByRole("presentation")
  for (const html of htmlToBeContained) {
    expect(snackbar).toContainHTML(html)
  }
}
