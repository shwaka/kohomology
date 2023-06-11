import { Delete, DragHandle } from "@mui/icons-material"
import { IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React from "react"
import { RowComponentProps } from "../DGAEditorDialog/SortableFields"

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
