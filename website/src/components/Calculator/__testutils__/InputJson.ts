import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { fireEvent, screen, waitFor, waitForElementToBeRemoved, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { findOrThrow } from "./findOrThrow"

// Used in both InputJson and InputArray
function openDialog(): HTMLElement {
  const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
  const editDGAButton = within(calculatorFormStackItemDGA).getByText("Edit DGA")
  expect(screen.queryByTestId("JsonEditorDialog-input-json")).not.toBeInTheDocument()
  fireEvent.click(editDGAButton)
  const dialog = screen.getByRole("dialog")
  return dialog
}

export class InputJson {
  private static async selectJsonTab(dialog: HTMLElement): Promise<void> {
    const tabs = within(dialog).getAllByRole("tab")
    const jsonTabButton = findOrThrow(tabs, (element) => (
      element?.textContent?.includes("JSON") ?? false
    ))
    fireEvent.click(jsonTabButton)
    await waitFor(() => {
      expect(jsonTabButton).toHaveAttribute("aria-selected", "true")
    })
  }

  private static async openDialogAndSelectJsonTab(): Promise<HTMLElement> {
    // Open dialog
    const dialog = openDialog()
    // Select the "JSON" tab
    await InputJson.selectJsonTab(dialog)
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
    const dialog = await InputJson.openDialogAndSelectJsonTab()
    InputJson.inputAndApplyJson(dialog, json)
    await waitForElementToBeRemoved(
      dialog,
      {
        // In local PC, timeout=800 is enough.
        // In GitHub Actions, this timed out with the default value (1000).
        timeout: 2000,
      },
    ) // It takes some time to remove the dialog.
  }

  static async inputInvalidJson(json: string): Promise<void> {
    const dialog = await InputJson.openDialogAndSelectJsonTab()
    InputJson.inputAndApplyJson(dialog, json)
    await within(dialog).findByRole("alert") // It takes some time to show alert.
  }
}

export type ApplyMethod = "button" | "enter"

export class InputArray {
  static async addGeneratorAndApply(applyMethod: ApplyMethod): Promise<void> {
    const dialog = openDialog()
    // default is the "Array" tab
    const addGeneratorButton = within(dialog).getByText("Add a generator")
    fireEvent.click(addGeneratorButton)
    switch (applyMethod) {
      case "button": {
        const applyButton = within(dialog).getByText("Apply")
        fireEvent.click(applyButton)
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
