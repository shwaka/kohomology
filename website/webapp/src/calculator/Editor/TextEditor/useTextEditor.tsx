
import { useForm } from "react-hook-form"

import { Editor, OnSubmit } from ".."
import { TextEditorProps, TextEditor, TextEditorFormInput } from "./TextEditor"

interface UseTextEditorArgs {
  text: string
  setText: (newText: string) => void
  preventPrompt: string
  fieldLabel: string
  fieldTestid?: string
  validate: (value: string) => true | string
}

export function useTextEditor(
  { text, setText, preventPrompt, fieldLabel, fieldTestid, validate }: UseTextEditorArgs
): Editor {
  const { register, handleSubmit, getValues, reset, trigger, formState: { errors } } = useForm<TextEditorFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    shouldUnregister: false, // necessary for setValue with MUI
    defaultValues: { text },
  })
  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return handleSubmit(
      ({ text: formText }) => {
        setText(formText)
        closeDialog()
      }
    )
  }
  function beforeOpen(): void {
    reset({ text })
  }
  function preventQuit(): string | undefined {
    if (getValues().text !== text) {
      return preventPrompt
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return errors.text !== undefined
  }
  const textEditorProps: TextEditorProps = {
    register, errors, trigger, fieldLabel, fieldTestid, validate,
  }
  return {
    preventQuit, getOnSubmit, beforeOpen, disableSubmit,
    renderContent: (_closeDialog) => (<TextEditor {...textEditorProps} />),
  }
}
