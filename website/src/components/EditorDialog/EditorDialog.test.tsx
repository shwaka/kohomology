import userEvent from "@testing-library/user-event"
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react"
import React from "react"
import { Editor } from "./Editor"
import { EditorDialog } from "./EditorDialog"
import { useEditorDialog } from "./useEditorDialog"

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

async function openDialog(): Promise<HTMLElement> {
  const openButton = screen.getByText("Open dialog")
  fireEvent.click(openButton)

  await waitFor(() => {
    expect(screen.getByRole("dialog")).toBeInTheDocument()
  })

  const dialog = screen.getByRole("dialog")
  return dialog
}

async function apply(dialog: HTMLElement): Promise<void> {
  const applyButton = within(dialog).getByText("Apply")
  fireEvent.click(applyButton)
}

async function cancel(dialog: HTMLElement): Promise<void> {
  const applyButton = within(dialog).getByText("Cancel")
  fireEvent.click(applyButton)
}

async function clickBackdrop(): Promise<void> {
  // eslint-disable-next-line testing-library/no-node-access
  const backdrop: Element | null = document.querySelector(".MuiBackdrop-root")
  expect(backdrop).toBeTruthy()
  if (backdrop === null) {
    throw new Error("This can't happen!")
  }
  await userEvent.click(backdrop)
}

describe("EditorDialog", () => {
  test("submit", async () => {
    const onSubmit = jest.fn()
    const editor: Editor = {
      renderContent: (_closeDialog) => (<div>Content of editor</div>),
      getOnSubmit: (closeDialog) => async (e) => {
        onSubmit(e),
        closeDialog()
      },
    }
    render(<EditorDialogTestContainer editor={editor}/>)

    const dialog = await openDialog()
    await apply(dialog)
    await assertClosed(dialog)

    expect(onSubmit).toHaveBeenCalled()
  })

  test("cancel", async () => {
    const onSubmit = jest.fn()
    const editor: Editor = {
      renderContent: (_closeDialog) => (<div>Content of editor</div>),
      getOnSubmit: (closeDialog) => async (e) => {
        onSubmit(e),
        closeDialog()
      },
    }
    render(<EditorDialogTestContainer editor={editor}/>)

    const dialog = await openDialog()
    await cancel(dialog)
    await assertClosed(dialog)

    expect(onSubmit).not.toHaveBeenCalled()
  })

  test("cancel by clicking exterior", async () => {
    const onSubmit = jest.fn()
    const editor: Editor = {
      renderContent: (_closeDialog) => (<div>Content of editor</div>),
      getOnSubmit: (closeDialog) => async (e) => {
        onSubmit(e),
        closeDialog()
      },
    }
    render(<EditorDialogTestContainer editor={editor}/>)

    const dialog = await openDialog()
    await clickBackdrop()
    await assertClosed(dialog)

    expect(onSubmit).not.toHaveBeenCalled()
  })
})
