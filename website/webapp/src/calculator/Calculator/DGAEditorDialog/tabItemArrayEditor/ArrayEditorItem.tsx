import React, { useCallback } from "react"

import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { RowComponentProps } from "@calculator/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { IconButton, InputBaseComponentProps, Stack, TextField, Tooltip } from "@mui/material"
import { useOverwritableTimeout } from "@site/src/utils/useOverwritableTimeout"
import { DeepRequired, FieldError, FieldErrorsImpl, FieldPath } from "react-hook-form"

import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

interface FieldOptions {
  key: string
  label: string
  width: number
  type?: React.HTMLInputTypeAttribute
  valueAsNumber?: true
  getRegisterName: (index: number) => FieldPath<GeneratorFormInput>
  isError: (errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number) => boolean
  inputProps?: InputBaseComponentProps
}


export function ArrayEditorItem(
  { draggableProps, index, formData: { register, errors, remove, getValues, trigger } }: RowComponentProps<GeneratorFormInput>
): React.JSX.Element {
  const generatorName = getValues().generatorArray[index].name

  const setOverwritableTimeout = useOverwritableTimeout()
  const triggerWithDelay = useCallback(
    () => setOverwritableTimeout(async () => await trigger(), 1000),
    [setOverwritableTimeout, trigger]
  )

  const fieldOptionsList: FieldOptions[] = [
    {
      key: "name",
      label: "generator",
      width: 90,
      getRegisterName: (index) => `generatorArray.${index}.name` as const,
      isError: (errors, index) => containsError({ errors, index, key: "name" }),
      inputProps: { "data-testid": "ArrayEditor-input-name" },
    },
    {
      key: "degree",
      label: `deg(${generatorName})`,
      width: 80,
      type: "number", valueAsNumber: true,
      getRegisterName: (index) => `generatorArray.${index}.degree` as const,
      isError: (errors, index) => containsError({ errors, index, key: "degree" }) || containsGlobalDegreeError({ errors }),
      inputProps: { "data-testid": "ArrayEditor-input-degree" },
    },
    {
      key: "differentialValue",
      label: `d(${generatorName})`,
      width: 200,
      getRegisterName: (index) => `generatorArray.${index}.differentialValue` as const,
      isError: (errors, index) => containsError({ errors, index, key: "differentialValue" }),
      inputProps: { "data-testid": "ArrayEditor-input-differentialValue" },
    },
  ]

  return (
    <div data-testid="ArrayEditor-row">
      <Stack spacing={1}>
        <Stack direction="row" spacing={1}>
          {fieldOptionsList.map(({ key, label, width, type, valueAsNumber, getRegisterName, isError, inputProps }) => (
            <TextField
              key={key}
              label={label} type={type}
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
              inputProps={inputProps}
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
        <ShowErrorsAtIndex errors={errors} index={index}/>
      </Stack>
    </div>
  )
}

function ShowErrorsAtIndex({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}): React.JSX.Element {
  const error = errors.generatorArray?.[index]
  const fieldErrorArray = getFieldErrorArray({ error })
  return (
    <ShowFieldErrors fieldErrorArray={fieldErrorArray}/>
  )
}

function getFieldErrorArray(
  { error }: { error: FieldErrorsImpl<Generator> | undefined }
): (FieldError | undefined)[] {
  if (error === undefined) {
    return []
  }
  return (["name", "degree", "differentialValue"] as const).flatMap((key) => error[key])
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function containsGlobalDegreeError({ errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }): boolean {
  return (errors._global_errors?.generatorDegrees !== undefined)
}
