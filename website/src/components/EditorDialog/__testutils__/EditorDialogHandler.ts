import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { screen, waitFor, within, act } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"

interface EditorDialogHandlerArgs {
  openDialogButtonText?: string
}

export const cancelMethods = ["button", "backdrop"] as const
export type CancelMethod = (typeof cancelMethods)[number]

type assertionType = "change" | "remain"
const remainingMs = 1000

function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export class EditorDialogHandler {
  user: UserEvent
  dialog: HTMLElement | null
  private openDialogButtonText: string
  private errorMessageWhenDialogIsNull: string =
    "The property dialog is null. Please call handler.openDialog first."

  constructor(
    {
      openDialogButtonText = "Open dialog",
    }: EditorDialogHandlerArgs = {}
  ) {
    this.user = userEvent.setup()
    this.dialog = null
    this.openDialogButtonText = openDialogButtonText
  }

  async openDialog(): Promise<void> {
    const openButton = screen.getByText("Open dialog")
    await this.user.click(openButton)

    await waitFor(() => {
      expect(screen.getByRole("dialog")).toBeInTheDocument()
    })

    this.dialog = screen.getByRole("dialog")
  }

  async apply(): Promise<void> {
    if (this.dialog === null) {
      throw new Error(this.errorMessageWhenDialogIsNull)
    }
    const applyButton = within(this.dialog).getByText("Apply")
    await this.user.click(applyButton)
  }

  async cancel(cancelMethod: CancelMethod): Promise<void> {
    if (this.dialog === null) {
      throw new Error(this.errorMessageWhenDialogIsNull)
    }
    switch (cancelMethod) {
      case "button": {
        const applyButton = within(this.dialog).getByText("Cancel")
        await this.user.click(applyButton)
        return
      }
      case "backdrop": {
        // eslint-disable-next-line testing-library/no-node-access
        const backdrop: Element | null = document.querySelector(".MuiBackdrop-root")
        expect(backdrop).toBeTruthy()
        if (backdrop === null) {
          throw new Error("This can't happen!")
        }
        await this.user.click(backdrop)
        return
      }
      default:
        throw new ExhaustivityError(cancelMethod)
    }
  }

  async assertOpen(
    assertionType: assertionType = "change"
  ): Promise<void> {
    if (this.dialog === null) {
      throw new Error(this.errorMessageWhenDialogIsNull)
    }
    if (assertionType === "remain") {
      await act(async () => {
        await sleep(remainingMs)
      })
    }
    await waitFor(() => {
      expect(this.dialog).toBeInTheDocument()
    })
  }

  async assertClosed(
    assertionType: assertionType = "change"
  ): Promise<void> {
    if (this.dialog === null) {
      throw new Error(this.errorMessageWhenDialogIsNull)
    }
    if (assertionType === "remain") {
      await sleep(remainingMs)
    }
    await waitFor(() => {
      expect(this.dialog).not.toBeInTheDocument()
    })
  }

  async run(
    func: (user: UserEvent, dialog: HTMLElement) => Promise<void>
  ): Promise<void> {
    if (this.dialog === null) {
      throw new Error(this.errorMessageWhenDialogIsNull)
    }
    await func(this.user, this.dialog)
  }
}
