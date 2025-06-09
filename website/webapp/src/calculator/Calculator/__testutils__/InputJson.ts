import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { screen, waitFor, waitForElementToBeRemoved, within } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"

import { findOrThrow } from "./findOrThrow"

function escapeForUserEventType(text: string): string {
  // escape special characters for user.type
  return text.replace(/\[/g, "[[").replace(/{/g, "{{")
}

// Used in both InputJson and InputArray
async function openDialog(user: UserEvent): Promise<HTMLElement> {
  const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
  const editDGAButton = within(calculatorFormStackItemDGA).getByText("Edit DGA")
  expect(screen.queryByTestId("JsonEditorDialog-input-json")).not.toBeInTheDocument()
  await user.click(editDGAButton)
  const dialog = screen.getByRole("dialog")
  return dialog
}

export class InputJson {
  private static async selectJsonTab(user: UserEvent, dialog: HTMLElement): Promise<void> {
    const tabs = within(dialog).getAllByRole("tab")
    const jsonTabButton = findOrThrow(tabs, (element) => (
      element?.textContent?.includes("JSON") ?? false
    ))
    await user.click(jsonTabButton)
    await waitFor(() => {
      expect(jsonTabButton).toHaveAttribute("aria-selected", "true")
    })
  }

  private static async openDialogAndSelectJsonTab(user: UserEvent): Promise<HTMLElement> {
    // Open dialog
    const dialog = await openDialog(user)
    // Select the "JSON" tab
    await InputJson.selectJsonTab(user, dialog)
    return dialog
  }

  private static async inputAndApplyJson(user: UserEvent, dialog: HTMLElement, json: string): Promise<void> {
    // Input json
    if (json !== "") {
      const jsonTextField = within(dialog).getByTestId("JsonEditorDialog-input-json")
      await user.clear(jsonTextField)
      await user.type(jsonTextField, escapeForUserEventType(json))
    }
    // Click "Apply" button
    const applyButton = within(dialog).getByText("Apply")
    await user.click(applyButton)
  }

  static async inputValidJson(user: UserEvent, json: string): Promise<void> {
    const dialog = await InputJson.openDialogAndSelectJsonTab(user)
    await InputJson.inputAndApplyJson(user, dialog, json)
    await waitForElementToBeRemoved(
      dialog,
      {
        // In local PC, timeout=800 is enough.
        // In GitHub Actions, this timed out with the default value (1000).
        timeout: 2000,
      },
    ) // It takes some time to remove the dialog.
  }

  static async inputInvalidJson(user: UserEvent, json: string): Promise<void> {
    const dialog = await InputJson.openDialogAndSelectJsonTab(user)
    await InputJson.inputAndApplyJson(user, dialog, json)
    await within(dialog).findByRole("alert") // It takes some time to show alert.
  }
}

export type ApplyMethod = "button" | "enter"

export class InputArray {
  static async addGeneratorAndApply(user: UserEvent, applyMethod: ApplyMethod): Promise<void> {
    const dialog = await openDialog(user)
    // default is the "Array" tab
    const addGeneratorButton = within(dialog).getByText("Add a generator")
    await user.click(addGeneratorButton)
    switch (applyMethod) {
      case "button": {
        const applyButton = within(dialog).getByText("Apply")
        await user.click(applyButton)
        break
      }
      case "enter": {
        const input = within(dialog).getAllByTestId("ArrayEditor-input-name")
        await userEvent.type(input[0], "{enter}")
        break
      }
      default:
        throw new ExhaustivityError(applyMethod)
    }
    await waitForElementToBeRemoved(
      dialog,
      {
        // See the comment in InputJson.inputValidJson
        timeout: 2000,
      },
    )
  }
}
