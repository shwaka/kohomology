import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import React, { ReactNode, useCallback } from "react"
import { Control, DeepRequired, FieldErrorsImpl, useFieldArray, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { FormData, SortableFields } from "../DGAEditorDialog/SortableFields"
import { Generator, IdealEditorItem, IdealFormInput } from "./IdealEditorItem"

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
}

interface UseIdealEditorReturnValue {
  idealEditorProps: IdealEditorProps
  getOnSubmit: (closeDialog: () => void) => void
  beforeOpen: () => void
}

export function useIdealEditor({ idealJson, setIdealJson }: UseIdealEditorArgs): UseIdealEditorReturnValue {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<IdealFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson)
    }
  })

  const getOnSubmit = useCallback((closeDialog: () => void): void => {
    handleSubmit(
      ({ generatorArray }) => {
        setIdealJson(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )()
  }, [setIdealJson, handleSubmit])

  const beforeOpen = useCallback((): void => {
    const generatorArray = jsonToGeneratorArray(idealJson)
    reset({ generatorArray })
  }, [idealJson, reset])

  const idealEditorProps: IdealEditorProps = {
    register, getValues, errors, trigger, control,
  }

  return {
    idealEditorProps, getOnSubmit, beforeOpen,
  }
}

function SortableFieldsContainer({ children }: { children: ReactNode }): JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}

export interface IdealEditorProps {
  register: UseFormRegister<IdealFormInput>
  getValues: UseFormGetValues<IdealFormInput>
  errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>
  trigger: UseFormTrigger<IdealFormInput>
  control: Control<IdealFormInput>
}

export function IdealEditor({ register, getValues, errors, trigger, control }: IdealEditorProps): JSX.Element {
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })
  const formData: FormData<IdealFormInput> = {
    register, remove, errors, getValues, trigger,
  }

  return (
    <div>
      <SortableFields
        RowComponent={IdealEditorItem}
        Container={SortableFieldsContainer}
        externalData={undefined}
        {...{ fields, move, formData }}
      />
      <Button
        variant="outlined"
        onClick={() => append({ text: "" })}
        startIcon={<Add/>}
        sx={{ textTransform: "none" }}
      >
        Add a generator
      </Button>
    </div>
  )
}
