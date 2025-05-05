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

    const openButton = screen.getByText("Open dialog")
    fireEvent.click(openButton)

    const dialog = screen.getByRole("dialog")
    const applyButton = within(dialog).getByText("Apply")
    fireEvent.click(applyButton)

    await waitFor(() => {
      expect(dialog).not.toBeInTheDocument()
    })

    expect(onSubmit).toHaveBeenCalled()
  })
})
