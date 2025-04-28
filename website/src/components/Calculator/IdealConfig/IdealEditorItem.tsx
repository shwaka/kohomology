import { ShowFieldErrors } from "@components/ShowFieldErrors"
import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { useCallback } from "react"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"
import { useOverwritableTimeout } from "../DGAEditorDialog/useOverwritableTimeout"
import { IdealFormInput } from "./schema"

export type ExternalData = Record<string, never>

export function IdealEditorItem(
  { draggableProps, index, formData: { register, errors, remove, trigger } }: RowComponentProps<IdealFormInput, ExternalData>
): React.JSX.Element {
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
          inputProps={{"data-testid": `IdealEditorItem-input-${index}`}}
          sx={{ width: 300 }} size="small"
          {...register(
            `generatorArray.${index}.text` as const,
            {
              onChange: triggerWithDelay,
            }
          )}
        />
        <Tooltip title="Delete this generator">
          <IconButton
            onClick={async () => { remove(index); await trigger() }}
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
      <ShowFieldErrors fieldErrorArray={getFieldErrorArray({ errors, index })}/>
    </Stack>
  )
}

function getFieldErrorArray({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>, index: number}): (FieldError | undefined)[] {
  const fieldError: FieldError | undefined = errors.generatorArray?.[index]?.text
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
