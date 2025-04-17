import { useFieldArray, useForm } from "react-hook-form"
import { TabItem } from "../TabDialog"
import { ArrayEditorProps } from "./ArrayEditor"
import { Generator, GeneratorFormInput, generatorArrayToJson } from "./ArrayEditorItem"

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as [string, number, string][]
  return arr.map(([name, degree, differentialValue]) => ({ name, degree, differentialValue }))
}

type UseArrayEditorReturnValue = Omit<TabItem, "render"> & {
  arrayEditorPropsExceptSubmit: Omit<ArrayEditorProps, "submit">
}

export function useArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): UseArrayEditorReturnValue {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<GeneratorFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(args.json)
    }
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })

  function onSubmit(closeDialog: () => void): void {
    handleSubmit(
      ({generatorArray}) => {
        args.updateDgaWrapper(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )()
  }
  function beforeOpen(): void {
    const generatorArray = jsonToGeneratorArray(args.json)
    reset({ generatorArray })
  }
  function preventQuit(): string | undefined {
    const generatorArray = getValues().generatorArray
    if (generatorArrayToJson(generatorArray) !== args.json) {
      return "Your input is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return (errors.generatorArray !== undefined) || (errors.dummy !== undefined)
  }
  const arrayEditorPropsExceptSubmit: Omit<ArrayEditorProps, "submit"> = {
    register, errors, fields, append, remove, getValues, trigger, move,
  }
  return {
    label: "Array",
    onSubmit,
    beforeOpen,
    preventQuit,
    disableSubmit,
    arrayEditorPropsExceptSubmit,
  }
}
