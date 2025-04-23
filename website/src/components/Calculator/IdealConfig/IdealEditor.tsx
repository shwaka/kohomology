import { FormData, SortableFields } from "@components/SortableFields"
import { OnSubmit } from "@components/TabDialog"
import { Add } from "@mui/icons-material"
import { Alert, Button, Stack } from "@mui/material"
import React, { ReactNode, useCallback } from "react"
import { Control, DeepRequired, FieldError, FieldErrorsImpl, MultipleFieldErrors, useFieldArray, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { ExternalData, Generator, IdealEditorItem, IdealFormInput } from "./IdealEditorItem"

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as string[]
  return arr.map((text) => ({ text }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(({ text }) => text)
  return JSON.stringify(arr)
}

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

function SortableFieldsContainer({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}

function getGlobalError(errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>): React.JSX.Element | undefined {
  if (errors.generatorArray !== undefined) {
    return undefined
  }
  // The following is the same as getGlobalError in tabItemArrayEditor.tsx
  const fieldError: FieldError | undefined = errors.dummy
  if (fieldError === undefined) {
    return undefined
  }
  const types: MultipleFieldErrors | undefined = fieldError.types
  if (types === undefined) {
    return undefined
  }
  return (
    <React.Fragment>
      {Object.entries(types).map(([errorType, message]) => (
        <Alert severity="error" key={errorType}>
          {message}
        </Alert>
      ))}
    </React.Fragment>
  )
}

export interface IdealEditorProps {
  register: UseFormRegister<IdealFormInput>
  getValues: UseFormGetValues<IdealFormInput>
  errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>
  trigger: UseFormTrigger<IdealFormInput>
  control: Control<IdealFormInput>
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealEditor({ register, getValues, errors, trigger, control, validateGenerator, validateGeneratorArray }: IdealEditorProps): React.JSX.Element {
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })
  const formData: FormData<IdealFormInput> = {
    register, remove, errors, getValues, trigger,
  }

  const externalData: ExternalData = { validateGenerator }

  return (
    <div>
      <Stack spacing={2} sx={{ marginTop: 1 }}>
        <SortableFields
          RowComponent={IdealEditorItem}
          Container={SortableFieldsContainer}
          {...{ fields, move, formData, externalData }}
        />
        <Button
          variant="outlined"
          onClick={() => append({ text: "" })}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <input
          hidden value="dummy"
          {...register("dummy", {
            validate: (_) => {
              const generatorArray = getValues().generatorArray.map((generator) => generator.text)
              return validateGeneratorArray(generatorArray)
            },
          })}
        />
        {getGlobalError(errors)}
      </Stack>
    </div>
  )
}
