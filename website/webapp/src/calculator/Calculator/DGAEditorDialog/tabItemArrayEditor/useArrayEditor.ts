import { OnSubmit, Editor } from "@calculator/EditorDialog"
import { zodResolver } from "@hookform/resolvers/zod"
import { DeepRequired, FieldError, FieldErrorsImpl, useFieldArray, useForm } from "react-hook-form"

import { ArrayEditorProps } from "./ArrayEditor"
import { ArrayEditorItem } from "./ArrayEditorItem"
import { generatorArrayToJson, jsonToGeneratorArray } from "./schema/ConvertGenerator"
import { formValueSchema, GeneratorFormInput, globalErrorsSchema } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

type UseArrayEditorReturnValue = {
  label: string
  editorWithoutRender: Omit<Editor, "renderContent">
  arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<GeneratorFormInput, "generatorArray">, "onSubmit">
}

export function useArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): UseArrayEditorReturnValue {
  const defaultValues: GeneratorFormInput = {
    generatorArray: jsonToGeneratorArray(args.json)
  }
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors, isValid } } = useForm({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues,
    resolver: zodResolver(formValueSchema),
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })

  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return handleSubmit(
      ({generatorArray}) => {
        args.updateDgaWrapper(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )
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
    return !isValid
    // Since we also have errors._global_errors, the following does not work.
    // return (errors.generatorArray !== undefined)
  }

  const arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<GeneratorFormInput, "generatorArray">, "onSubmit"> = {
    register, errors, fields, append, remove, getValues, trigger, move, getGlobalErrors, getNext,
    RowComponent: ArrayEditorItem, arrayKey: "generatorArray",
  }
  return {
    label: "Array",
    editorWithoutRender: {
      getOnSubmit,
      beforeOpen,
      preventQuit,
      disableSubmit,
    },
    arrayEditorPropsExceptOnSubmit,
  }
}

function getGlobalErrors(
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
): (FieldError | undefined)[] {
  const _global_errors = errors._global_errors
  if (_global_errors === undefined) {
    return []
  }
  const keys = Object.keys(globalErrorsSchema.shape) as (keyof typeof globalErrorsSchema.shape)[]
  return keys.map((key) => _global_errors[key] )
}

function getNameOfNextGenerator(generatorArray: Generator[]): string {
  const existingNames: string[] = generatorArray.map((generator) => generator.name)
  // "d" cannot be used since it represents the differential
  const nameCandidates: string[] = "xyzuvwabc".split("")
  for (const candidate of nameCandidates) {
    if (!existingNames.includes(candidate)) {
      return candidate
    }
  }
  return ""
}

function getNext(generatorArray: Generator[]): Generator {
  return {
    name: getNameOfNextGenerator(generatorArray),
    degree: 1,
    differentialValue: "0"

  }
}
