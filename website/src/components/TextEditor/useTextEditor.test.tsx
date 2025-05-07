import { EditorDialog, useEditorDialog } from "@components/EditorDialog"
import { render, screen, waitFor, within } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"
import React, { useState } from "react"
import { useTextEditor } from "./useTextEditor"
import { EditorDialogHandler } from "@components/EditorDialog/__testutils__/EditorDialogHandler"

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

async function inputText(
  user: UserEvent, dialog: HTMLElement, text: string
): Promise<void> {
  const input = within(dialog).getByTestId(fieldTestid)
  await user.type(input, text)
}

describe("useTextEditor", () => {
  beforeEach(() => {
    mockConfirm.mockReset()
  })

  test("inputting text", async () => {
    const handler = new EditorDialogHandler()
    render(<TextEditorContainer defaultText=""/>)
    const newText = "This is a new text."
    await handler.openDialog()
    await handler.run(async (user, dialog) => {
      await inputText(user, dialog, newText)
    })
    await handler.apply()

    const textDiv = screen.getByTestId("text-div")
    expect(textDiv).toContainHTML(newText)
  })
})
