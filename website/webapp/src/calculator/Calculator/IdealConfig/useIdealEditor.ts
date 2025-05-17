import { OnSubmit } from "@calculator/EditorDialog"
import { zodResolver } from "@hookform/resolvers/zod"
import { useCallback } from "react"
import { useFieldArray, useForm } from "react-hook-form"
import { IdealEditorProps } from "./IdealEditor"
import { Generator, getFormValueSchema } from "./schema"

export interface UseIdealEditorArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

interface UseIdealEditorReturnValue {
  idealEditorPropsExceptOnSubmit: Omit<IdealEditorProps, "onSubmit">
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  beforeOpen: () => void
  disableSubmit: () => boolean
  preventQuit: () => string | undefined
}

export function useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray }: UseIdealEditorArgs): UseIdealEditorReturnValue {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson)
    },
    resolver: zodResolver(
      getFormValueSchema(validateGenerator, validateGeneratorArray),
      undefined, // schemaOptions?: Partial<z.ParseParams>
      { mode: "async" }, // resolverOptions: { mode?: 'async' | 'sync', raw?: boolean }
    )
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })

  const getOnSubmit = useCallback((closeDialog: () => void): OnSubmit => {
    return handleSubmit(
      ({ generatorArray }) => {
        setIdealJson(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )
  }, [setIdealJson, handleSubmit])

  const beforeOpen = useCallback((): void => {
    const generatorArray = jsonToGeneratorArray(idealJson)
    reset({ generatorArray })
  }, [idealJson, reset])

  const idealEditorPropsExceptOnSubmit: Omit<IdealEditorProps, "onSubmit"> = {
    register, getValues, errors, trigger,
    fields, append, remove, move,
  }

  const disableSubmit = useCallback((): boolean => {
    return (errors.generatorArray !== undefined) || (errors._global_errors !== undefined)
  }, [errors])

  const preventQuit = useCallback((): string | undefined =>  {
    const generatorArray = getValues().generatorArray
    if (generatorArrayToJson(generatorArray) !== idealJson) {
      return "Your input is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }, [getValues, idealJson])

  return {
    idealEditorPropsExceptOnSubmit, getOnSubmit, beforeOpen, disableSubmit, preventQuit,
  }
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as string[]
  return arr.map((text) => ({ text }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(({ text }) => text)
  return JSON.stringify(arr)
}
