import { fireEvent, screen, waitForElementToBeRemoved, within } from "@testing-library/react"

export class InputJson {
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
