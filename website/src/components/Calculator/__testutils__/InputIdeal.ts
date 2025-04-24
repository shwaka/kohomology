import { fireEvent, screen, waitForElementToBeRemoved, within } from "@testing-library/react"

export class InputIdeal {
  private static openDialog(): HTMLElement {
    const stackItem = screen.getByTestId("CalculatorForm-StackItem-SelectTarget")
    const editIdealButton = within(stackItem).getByText("Edit ideal")
    fireEvent.click(editIdealButton)
    const dialog = screen.getByRole("dialog")
    return dialog
  }

  private static apply(dialog: HTMLElement): void {
    const applyButton = within(dialog).getByText("Apply")
    fireEvent.click(applyButton)
  }

  static async inputIdealGenerator(generatorArray: string[]): Promise<void> {
    const dialog = InputIdeal.openDialog()
    const addGeneratorButton = within(dialog).getByText("Add a generator")
    generatorArray.forEach((generator, index) => {
      fireEvent.click(addGeneratorButton)
      const input = within(dialog).getByTestId(`IdealEditorItem-input-${index}`)
      fireEvent.input(input, { target: { value: generator }})
    })
    InputIdeal.apply(dialog)
    // See comments in InputJson.inputValidJson
    await waitForElementToBeRemoved(
      dialog,
      {
        timeout: 2000,
      },
    )
  }
}
