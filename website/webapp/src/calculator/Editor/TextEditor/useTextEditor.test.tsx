import React, { useState } from "react"

import { EditorDialog, useEditorDialog } from ".."
import { cancelMethods, EditorDialogHandler } from "../EditorDialog/__testutils__/EditorDialogHandler"
import { render, screen, waitFor, within } from "@testing-library/react"
import { UserEvent } from "@testing-library/user-event"

import { useTextEditor } from "./useTextEditor"

jest.mock("@calculator/ConfirmDialog/useConfirm")

// The following import works for jest, but not for tsc.
//   import { mockConfirm } from "@calculator/ConfirmDialog/useConfirm"
const { mockConfirm } = jest.requireMock("@calculator/ConfirmDialog/useConfirm") as {
  mockConfirm: jest.Mock
}

interface TextEditorContainerProps {
  defaultText: string
  validate?: (value: string) => true | string
}

const preventPrompt = "Do you really want to quit?"
const fieldLabel = "Text field"
const fieldTestid = "text-field"
const textFromContainer = "This is a text from container."

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
      <button onClick={() => setText(textFromContainer)}>
        Set text
      </button>
      <div data-testid="text-div">
        {text}
      </div>
    </div>
  )
}

async function clearText(
  user: UserEvent, dialog: HTMLElement
): Promise<void> {
  const input = within(dialog).getByTestId(fieldTestid)
  await user.clear(input)
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

  test("input text", async () => {
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

  test("default text", async () => {
    const defaultText = "This is default."
    const handler = new EditorDialogHandler()
    render(<TextEditorContainer defaultText={defaultText}/>)

    const textDiv = screen.getByTestId("text-div")
    expect(textDiv).toContainHTML(defaultText)

    await handler.openDialog()
    await handler.run(async (_user, dialog) => {
      const input = within(dialog).getByTestId(fieldTestid)
      expect(input).toHaveValue(defaultText)
    })

    const newText = "This is a new text."
    await handler.run(async (user, dialog) => {
      await clearText(user, dialog)
      await inputText(user, dialog, newText)
    })
    await handler.apply()

    expect(textDiv).toContainHTML(newText)
  })

  for (const cancelMethod of cancelMethods) {
    test(`cancel with ${cancelMethod}`, async () => {
      mockConfirm.mockResolvedValue(true)
      const defaultText = "This is default."
      const handler = new EditorDialogHandler()
      render(<TextEditorContainer defaultText={defaultText}/>)
      const newText = "This is a new text."
      await handler.openDialog()
      await handler.run(async (user, dialog) => {
        await inputText(user, dialog, newText)
      })
      await handler.cancel(cancelMethod)
      await handler.assertClosed()

      const textDiv = screen.getByTestId("text-div")
      expect(textDiv).toContainHTML(defaultText)
      expect(textDiv).not.toContainHTML(newText)
    })
  }

  for (const cancelMethod of cancelMethods) {
    test(`resume with ${cancelMethod}`, async () => {
      mockConfirm.mockResolvedValue(false)
      const defaultText = "This is default."
      const handler = new EditorDialogHandler()
      render(<TextEditorContainer defaultText={defaultText}/>)
      const newText = "This is a new text."
      await handler.openDialog()
      await handler.run(async (user, dialog) => {
        await clearText(user, dialog)
        await inputText(user, dialog, newText)
      })
      await handler.cancel(cancelMethod)
      await handler.assertOpen("remain")
      await handler.apply()

      const textDiv = screen.getByTestId("text-div")
      expect(textDiv).toContainHTML(newText)
      expect(textDiv).not.toContainHTML(defaultText)
    })
  }

  test("set text from container", async () => {
    const defaultText = "This is default."
    const handler = new EditorDialogHandler()
    render(<TextEditorContainer defaultText={defaultText}/>)

    const textDiv = screen.getByTestId("text-div")
    expect(textDiv).toContainHTML(defaultText)

    await handler.openDialog()
    const newText = "This is a new text."
    await handler.run(async (user, dialog) => {
      await clearText(user, dialog)
      await inputText(user, dialog, newText)
    })
    await handler.apply()

    expect(textDiv).toContainHTML(newText)
    expect(textDiv).not.toContainHTML(defaultText)

    await handler.run(async (user, _dialog) => {
      const button = screen.getByText("Set text")
      await user.click(button)
    })
    await waitFor(() => {
      expect(textDiv).toContainHTML(textFromContainer)
    })
    expect(textDiv).not.toContainHTML(newText)

    await handler.openDialog()
    await handler.run(async (_user, dialog) => {
      const input = within(dialog).getByTestId(fieldTestid)
      expect(input).toHaveValue(textFromContainer)
    })
  })

  test("validate returning string", async () => {
    const handler = new EditorDialogHandler()
    const errorMessage = "Your text contains an error."
    const validate = jest.fn().mockReturnValue(errorMessage)
    render(<TextEditorContainer defaultText={""} validate={validate}/>)

    await handler.openDialog()
    await handler.apply()
    await handler.assertOpen("remain")
    await handler.run(async (_user, dialog) => {
      expect(dialog).toContainHTML(errorMessage)
    })
  })

  test("validate returning true", async () => {
    const handler = new EditorDialogHandler()
    const validate = jest.fn().mockReturnValue(true)
    render(<TextEditorContainer defaultText={""} validate={validate}/>)

    await handler.openDialog()
    await handler.apply()
    await handler.assertClosed("change")
  })
})
