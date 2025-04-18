import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { Alert, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { useCallback } from "react"
import { DeepRequired, FieldErrorsImpl } from "react-hook-form"
import { useOverwritableTimeout } from "../DGAEditorDialog/useOverwritableTimeout"

export interface Generator {
  text: string
}

export interface IdealFormInput {
  dummy: "dummy"
  generatorArray: Generator[]
}

export interface ExternalData {
  validateGenerator: (generator: string) => Promise<true | string>
}

function getFieldError({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>, index: number}): JSX.Element | undefined {
  const error = errors.generatorArray?.[index]
  if (error === undefined) {
    return undefined
  }
  const errorMessage = error.text?.message
  if (errorMessage === undefined) {
    return undefined
  }
  return (
    <Alert severity="error" sx={{ whiteSpace: "pre-wrap" }}>
      {errorMessage}
    </Alert>
  )
}

export function IdealEditorItem(
  { draggableProps, index, formData: { register, errors, remove, trigger }, externalData: { validateGenerator } }: RowComponentProps<IdealFormInput, ExternalData>
): JSX.Element {
  const setOverwritableTimeout = useOverwritableTimeout()
  const triggerWithDelay = useCallback(
    () => setOverwritableTimeout(async () => await trigger(), 1000),
    [setOverwritableTimeout, trigger]
  )

  return (
    <Stack spacing={1}>
      <Stack direction="row" spacing={1}>
        <TextField
          label="generator"
          sx={{ width: 300 }} size="small"
          {...register(
            `generatorArray.${index}.text` as const,
            {
              required: "Please enter the generator.",
              validate: validateGenerator,
              onChange: triggerWithDelay,
            }
          )}
        />
        <Tooltip title="Delete this generator">
          <IconButton
            onClick={() => { remove(index); trigger() }}
            size="small"
          >
            <Delete fontSize="small"/>
          </IconButton>
        </Tooltip>
        <IconButton
          {...draggableProps}
          style={{
            cursor: "grab",
            touchAction: "none",
          }}
        >
          <DragHandle/>
        </IconButton>
      </Stack>
      {getFieldError({ errors, index })}
    </Stack>
  )
}
