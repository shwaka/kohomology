import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { render, screen, waitFor, within } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"
import React from "react"
import { Editor } from "./Editor"
import { EditorDialog } from "./EditorDialog"
import { OnSubmit } from "./OnSubmit"
import { useEditorDialog } from "./useEditorDialog"
import { cancelMethods, EditorDialogHandler } from "./__testutils__/EditorDialogHandler"

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
    const handler = new EditorDialogHandler()
    const { editor, onSubmit, onQuit } = getEditor()
    render(<EditorDialogTestContainer editor={editor}/>)

    await handler.openDialog()
    await handler.apply()
    await handler.assertClosed()

    expect(onSubmit).toHaveBeenCalled()
    expect(onQuit).not.toHaveBeenCalled()
  })

  for (const cancelMethod of cancelMethods) {
    test(`cancel(${cancelMethod})`, async () => {
      const handler = new EditorDialogHandler()
      const { editor, onSubmit, onQuit } = getEditor()
      render(<EditorDialogTestContainer editor={editor}/>)

      await handler.openDialog()
      await handler.cancel(cancelMethod)
      await handler.assertClosed()

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
      const handler = new EditorDialogHandler()
      mockConfirm.mockResolvedValue(true)
      const preventQuit = jest.fn().mockReturnValue("Do you really want to quit?")
      const { editor, onSubmit, onQuit } = getEditor({
        preventQuit,
      })
      render(<EditorDialogTestContainer editor={editor}/>)

      await handler.openDialog()
      await handler.cancel(cancelMethod)
      await handler.assertClosed()

      expect(preventQuit).toHaveBeenCalledOnce()
      expect(mockConfirm).toHaveBeenCalledOnce()
      expect(onSubmit).not.toHaveBeenCalled()
      expect(onQuit).toHaveBeenCalledOnce()
    })
  }

  for (const cancelMethod of cancelMethods) {
    test(`cancel(${cancelMethod}) with window.confirm returning false`, async () => {
      const handler = new EditorDialogHandler()
      mockConfirm.mockResolvedValue(false)
      const preventQuit = jest.fn().mockReturnValue("Do you really want to quit?")
      const { editor, onSubmit, onQuit } = getEditor({
        preventQuit,
      })
      render(<EditorDialogTestContainer editor={editor}/>)

      await handler.openDialog()
      await handler.cancel(cancelMethod)
      await handler.assertOpen()

      expect(preventQuit).toHaveBeenCalledOnce()
      expect(mockConfirm).toHaveBeenCalledOnce()
      expect(onSubmit).not.toHaveBeenCalled()
      expect(onQuit).not.toHaveBeenCalledOnce()
    })
  }
})
