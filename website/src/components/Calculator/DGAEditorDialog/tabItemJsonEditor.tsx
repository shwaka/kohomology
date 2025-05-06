import { TabItem } from "@components/TabDialog"
import { validateJson } from "kohomology-js"
import { useTextEditor } from "./TextEditor"

export function useTabItemJsonEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const editor = useTextEditor({
    text: args.json, setText: args.updateDgaWrapper,
    preventPrompt: "Your JSON is not saved. Are you sure you want to quit?",
    fieldLabel: "Input your DGA",
    fieldTestid: "JsonEditorDialog-input-json",
    validate,
  })
  return {
    label: "JSON",
    editor,
  }
}

function validate(value: string): true | string {
  const validationResult = validateJson(value)
  if (validationResult.type === "success") {
    return true
  } else {
    return validationResult.message
  }
}
