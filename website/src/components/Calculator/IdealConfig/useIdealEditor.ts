import { OnSubmit } from "@components/TabDialog"
import { useCallback } from "react"
import { useForm } from "react-hook-form"
import { IdealEditorProps } from "./IdealEditor"
import { Generator, IdealFormInput } from "./IdealEditorItem"

interface UseIdealEditorArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

interface UseIdealEditorReturnValue {
  idealEditorProps: IdealEditorProps
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  beforeOpen: () => void
  disableSubmit: () => boolean
  preventQuit: () => string | undefined
}

export function useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray }: UseIdealEditorArgs): UseIdealEditorReturnValue {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<IdealFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson)
    }
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

  const idealEditorProps: IdealEditorProps = {
    register, getValues, errors, trigger, control, validateGenerator, validateGeneratorArray,
  }

  const disableSubmit = useCallback((): boolean => {
    return (errors.generatorArray !== undefined) || (errors.dummy !== undefined)
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
    idealEditorProps, getOnSubmit, beforeOpen, disableSubmit, preventQuit,
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
