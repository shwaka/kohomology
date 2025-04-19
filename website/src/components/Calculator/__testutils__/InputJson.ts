import { fireEvent, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { findOrThrow } from "./findOrThrow"

export class InputJson {
  private static openDialog(): HTMLElement {
    // Open dialog
    const calculatorFormStackItemDGA = screen.getByTestId("CalculatorForm-StackItem-DGA")
    const editDGAButton = within(calculatorFormStackItemDGA).getByText("Edit DGA")
    expect(screen.queryByTestId("JsonEditorDialog-input-json")).not.toBeInTheDocument()
    fireEvent.click(editDGAButton)
    const dialog = screen.getByRole("dialog")
    // Select the "JSON" tab
    const tabs = within(dialog).getAllByRole("tab")
    const jsonTabButton = findOrThrow(tabs, (element) => (element?.textContent?.includes("JSON")))
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
    const dialog = InputJson.openDialog()
    InputJson.inputAndApplyJson(dialog, json)
    await within(dialog).findByRole("alert") // It takes some time to show alert.
  }
}
