import { Add, Delete, DragHandle } from "@mui/icons-material"
import { Alert, Button, Dialog, DialogActions, DialogContent, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { ReactNode, useCallback, useMemo, useState } from "react"
import { Control, DeepRequired, FieldArrayWithId, FieldErrorsImpl, useFieldArray, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { FormData, RowComponentProps, SortableFields } from "../DGAEditorDialog/SortableFields"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
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
