import { screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { UserEvent } from "@testing-library/user-event"

export class InputIdeal {
  private static async openDialog(user: UserEvent): Promise<HTMLElement> {
    const stackItem = screen.getByTestId("CalculatorForm-StackItem-SelectTarget")
    const editIdealButton = within(stackItem).getByText("Edit ideal")
    await user.click(editIdealButton)
    const dialog = screen.getByRole("dialog")
    return dialog
  }

  private static async apply(user: UserEvent, dialog: HTMLElement): Promise<void> {
    const applyButton = within(dialog).getByText("Apply")
    await user.click(applyButton)
  }

  private static async inputIdealGeneratorAndApply(user: UserEvent, dialog: HTMLElement, generatorArray: string[]): Promise<void> {
    const addGeneratorButton = within(dialog).getByText("Add a generator")
    for (const [index, generator] of generatorArray.entries()) {
      await user.click(addGeneratorButton)
      const input = within(dialog).getByTestId(`IdealEditorItem-input-${index}`)
      if (generator !== "") {
        await user.type(input, generator)
      }
    }
    await InputIdeal.apply(user, dialog)
  }

  static async inputValidIdealGenerator(user: UserEvent, generatorArray: string[]): Promise<void> {
    const dialog = await InputIdeal.openDialog(user)
    await InputIdeal.inputIdealGeneratorAndApply(user, dialog, generatorArray)
    // See comments in InputJson.inputValidJson
    await waitForElementToBeRemoved(
      dialog,
      {
        timeout: 2000,
      },
    )
  }

  static async inputInvalidIdealGenerator(user: UserEvent, generatorArray: string[]): Promise<void> {
    const dialog = await InputIdeal.openDialog(user)
    await InputIdeal.inputIdealGeneratorAndApply(user, dialog, generatorArray)
    await within(dialog).findByRole("alert")
  }
}
