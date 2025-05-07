import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { render, screen, waitFor, within } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"
import React from "react"
import { Editor } from "./Editor"
import { EditorDialog } from "./EditorDialog"
import { OnSubmit } from "./OnSubmit"
import { useEditorDialog } from "./useEditorDialog"

jest.mock("@components/ConfirmDialog/useConfirm")

// The following import works for jest, but not for tsc.
//   import { mockConfirm } from "@components/ConfirmDialog/useConfirm"
const { mockConfirm } = jest.requireMock("@components/ConfirmDialog/useConfirm") as {
  mockConfirm: jest.Mock
}

interface ContainerProps {
  editor: Editor
}

function EditorDialogTestContainer({ editor }: ContainerProps): React.JSX.Element {
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })
  return (
    <div>
      <EditorDialog {...editorDialogProps}/>
      <button onClick={openDialog}>
        Open dialog
      </button>
    </div>
  )
}

async function assertOpen(dialog: HTMLElement): Promise<void> {
  await waitFor(() => {
    expect(dialog).toBeInTheDocument()
  })
}

async function assertClosed(dialog: HTMLElement): Promise<void> {
  await waitFor(() => {
    expect(dialog).not.toBeInTheDocument()
  })
}

async function openDialog(user: UserEvent): Promise<HTMLElement> {
  const openButton = screen.getByText("Open dialog")
  await user.click(openButton)

  await waitFor(() => {
    expect(screen.getByRole("dialog")).toBeInTheDocument()
  })

  const dialog = screen.getByRole("dialog")
  return dialog
}

async function apply(user: UserEvent, dialog: HTMLElement): Promise<void> {
  const applyButton = within(dialog).getByText("Apply")
  await user.click(applyButton)
}

const cancelMethods = ["button", "backdrop"] as const
type CancelMethod = (typeof cancelMethods)[number]

async function cancel(
  user: UserEvent,
  cancelMethod: CancelMethod,
  dialog: HTMLElement,
): Promise<void> {
  switch (cancelMethod) {
    case "button": {
      const applyButton = within(dialog).getByText("Cancel")
      await user.click(applyButton)
      return
    }
    case "backdrop": {
      // eslint-disable-next-line testing-library/no-node-access
      const backdrop: Element | null = document.querySelector(".MuiBackdrop-root")
      expect(backdrop).toBeTruthy()
      if (backdrop === null) {
        throw new Error("This can't happen!")
      }
      await user.click(backdrop)
      return
    }
    default:
      throw new ExhaustivityError(cancelMethod)
  }
}

interface GetEditorArgs {
  preventQuit?: () => (string | undefined)
  disableSubmit?: () => boolean
  beforeOpen?: () => void
}

interface GetEditorReturnValue {
  editor: Editor
  onSubmit: OnSubmit
  onQuit: () => void
}

function getEditor(
  {
    preventQuit = undefined,
    disableSubmit = undefined,
    beforeOpen = undefined,
  }: GetEditorArgs = {}
): GetEditorReturnValue {
  const onSubmit = jest.fn()
  const onQuit = jest.fn()
  const editor: Editor = {
    preventQuit, disableSubmit, beforeOpen,
    renderContent: (_closeDialog) => (<div>Content of editor</div>),
    getOnSubmit: (closeDialog) => async (e) => {
      onSubmit(e)
      closeDialog()
    },
    onQuit,
  }
  return { editor, onSubmit, onQuit }
}

describe("EditorDialog with trivial editor", () => {
  test("submit", async () => {
    const user = userEvent.setup()
    const { editor, onSubmit, onQuit } = getEditor()
    render(<EditorDialogTestContainer editor={editor}/>)

    const dialog = await openDialog(user)
    await apply(user, dialog)
    await assertClosed(dialog)

    expect(onSubmit).toHaveBeenCalled()
    expect(onQuit).not.toHaveBeenCalled()
  })

  for (const cancelMethod of cancelMethods) {
    test(`cancel(${cancelMethod})`, async () => {
      const user: UserEvent = userEvent.setup()
      const { editor, onSubmit, onQuit } = getEditor()
      render(<EditorDialogTestContainer editor={editor}/>)

      const dialog = await openDialog(user)
      await cancel(user, cancelMethod, dialog)
      await assertClosed(dialog)

      expect(onSubmit).not.toHaveBeenCalled()
      expect(onQuit).toHaveBeenCalled()
    })
  }
})

describe("EditorDialog with preventQuit returning string", () => {
  beforeEach(() => {
    mockConfirm.mockReset()
  })

  for (const cancelMethod of cancelMethods) {
    test(`cancel(${cancelMethod}) with window.confirm returning true`, async () => {
      const user = userEvent.setup()
      mockConfirm.mockResolvedValue(true)
      const preventQuit = jest.fn().mockReturnValue("Do you really want to quit?")
      const { editor, onSubmit, onQuit } = getEditor({
        preventQuit,
      })
      render(<EditorDialogTestContainer editor={editor}/>)

      const dialog = await openDialog(user)
      await cancel(user, cancelMethod, dialog)
      await assertClosed(dialog)

      expect(preventQuit).toHaveBeenCalledOnce()
      expect(mockConfirm).toHaveBeenCalledOnce()
      expect(onSubmit).not.toHaveBeenCalled()
      expect(onQuit).toHaveBeenCalledOnce()
    })
  }

  for (const cancelMethod of cancelMethods) {
    test(`cancel(${cancelMethod}) with window.confirm returning false`, async () => {
      const user = userEvent.setup()
      mockConfirm.mockResolvedValue(false)
      const preventQuit = jest.fn().mockReturnValue("Do you really want to quit?")
      const { editor, onSubmit, onQuit } = getEditor({
        preventQuit,
      })
      render(<EditorDialogTestContainer editor={editor}/>)

      const dialog = await openDialog(user)
      await cancel(user, cancelMethod, dialog)
      await assertOpen(dialog)

      expect(preventQuit).toHaveBeenCalledOnce()
      expect(mockConfirm).toHaveBeenCalledOnce()
      expect(onSubmit).not.toHaveBeenCalled()
      expect(onQuit).not.toHaveBeenCalledOnce()
    })
  }
})
