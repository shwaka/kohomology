import { EditorDialog, useEditorDialog } from "@components/EditorDialog"
import { render, screen, waitFor, within } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"
import React, { useState } from "react"
import { useTextEditor } from "./useTextEditor"

jest.mock("@components/ConfirmDialog/useConfirm")

// The following import works for jest, but not for tsc.
//   import { mockConfirm } from "@components/ConfirmDialog/useConfirm"
const { mockConfirm } = jest.requireMock("@components/ConfirmDialog/useConfirm") as {
  mockConfirm: jest.Mock
}

interface TextEditorContainerProps {
  defaultText: string
  validate?: (value: string) => true | string
}

const preventPrompt = "Do you really want to quit?"
const fieldLabel = "Text field"
const fieldTestid = "text-field"

function TextEditorContainer(
  { defaultText, validate = (_value) => true }: TextEditorContainerProps
): React.JSX.Element {
  const [text, setText] = useState<string>(defaultText)
  const editor = useTextEditor({
    text, setText, preventPrompt, fieldLabel, fieldTestid, validate
  })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })

  return (
    <div>
      <EditorDialog {...editorDialogProps}/>
      <button onClick={openDialog}>
        Open dialog
      </button>
      <div data-testid="text-div">
        {text}
      </div>
    </div>
  )
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

async function inputText(
  user: UserEvent, dialog: HTMLElement, text: string
): Promise<void> {
  const input = within(dialog).getByTestId(fieldTestid)
  await user.type(input, text)
}

async function apply(user: UserEvent, dialog: HTMLElement): Promise<void> {
  const applyButton = within(dialog).getByText("Apply")
  await user.click(applyButton)
}

describe("useTextEditor", () => {
  beforeEach(() => {
    mockConfirm.mockReset()
  })

  test("inputting text", async () => {
    const user = userEvent.setup()
    render(<TextEditorContainer defaultText=""/>)
    const newText = "This is a new text."
    const dialog = await openDialog(user)
    await inputText(user, dialog, newText)
    await apply(user, dialog)

    const textDiv = screen.getByTestId("text-div")
    expect(textDiv).toContainHTML(newText)
  })
})
