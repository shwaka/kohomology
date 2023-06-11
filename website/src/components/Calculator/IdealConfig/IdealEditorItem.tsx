import { Add, Delete, DragHandle } from "@mui/icons-material"
import { Alert, Button, Dialog, DialogActions, DialogContent, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { ReactNode, useCallback, useMemo, useState } from "react"
import { Control, DeepRequired, FieldArrayWithId, FieldErrorsImpl, useFieldArray, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { FormData, RowComponentProps, SortableFields } from "../DGAEditorDialog/SortableFields"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"

export interface Generator {
  text: string
}

export interface IdealFormInput {
  generatorArray: Generator[]
}

export function IdealEditorItem(
  { draggableProps, index, formData: { register, errors, remove, getValues, trigger } }: RowComponentProps<IdealFormInput>
): JSX.Element {
  return (
    <Stack direction="row" spacing={1}>
      <TextField
        label="generator"
        sx={{ width: 200 }} size="small"
        {...register(
          `generatorArray.${index}.text` as const,
          {
            required: "Please enter the generator.",
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
  )
}
