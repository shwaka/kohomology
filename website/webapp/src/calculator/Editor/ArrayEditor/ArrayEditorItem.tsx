import React, { useCallback, ReactElement } from "react"

import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { RowComponentProps } from "@calculator/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { IconButton, InputBaseComponentProps, Stack, TextField, Tooltip } from "@mui/material"
import { useOverwritableTimeout } from "@site/src/utils/useOverwritableTimeout"
import { DeepRequired, FieldError, FieldErrorsImpl, FieldPath, FieldValues } from "react-hook-form"

export interface FieldOptions<TFieldValues extends FieldValues> {
  key: string
  label: string | ((values: TFieldValues, index: number) => string)
  width: number
  type?: React.HTMLInputTypeAttribute
  valueAsNumber?: true
  getRegisterName: (index: number) => FieldPath<TFieldValues>
  isError: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>, index: number) => boolean
  inputProps?: InputBaseComponentProps | ((index: number) => InputBaseComponentProps)
}

export type ArrayEditorRowComponentData<TFieldValues extends FieldValues> = {
  fieldOptionsList: FieldOptions<TFieldValues>[]
  getFieldErrorArray: (args: { errors: FieldErrorsImpl<DeepRequired<TFieldValues>>, index: number}) => (FieldError | undefined)[]
}

export interface ArrayEditorItemProps<TFieldValues extends FieldValues> {
  rowComponentProps: RowComponentProps<TFieldValues>
  rowComponentData: ArrayEditorRowComponentData<TFieldValues>
}

export function ArrayEditorItem<TFieldValues extends FieldValues>({
  rowComponentProps,
  rowComponentData: { fieldOptionsList, getFieldErrorArray },
}: ArrayEditorItemProps<TFieldValues>): ReactElement {
  const { draggableProps, index, formData: { register, getValues, errors, remove, trigger } } = rowComponentProps
  const setOverwritableTimeout = useOverwritableTimeout()
  const triggerWithDelay = useCallback(
    () => setOverwritableTimeout(async () => await trigger(), 1000),
    [setOverwritableTimeout, trigger]
  )

  return (
    <div data-testid="ArrayEditor-row">
      <Stack spacing={1}>
        <Stack direction="row" spacing={1}>
          {fieldOptionsList.map(({ key, label, width, type, valueAsNumber, getRegisterName, isError, inputProps }) => (
            <TextField
              key={key}
              label={(typeof label === "function") ? label(getValues(), index) : label}
              type={type}
              sx={{ width }} size="small"
              {...register(
                getRegisterName(index),
                {
                  valueAsNumber,
                  onChange: triggerWithDelay,
                }
              )}
              onBlur={() => trigger()}
              error={isError(errors, index)}
              inputProps={(typeof inputProps === "function") ? inputProps(index) : inputProps}
            />
          ))}
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
    </div>
  )
}
