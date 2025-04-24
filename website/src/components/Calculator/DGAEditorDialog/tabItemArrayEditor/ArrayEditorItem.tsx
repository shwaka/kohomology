import { ShowFieldErrors } from "@components/ShowFieldErrors"
import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { useCallback } from "react"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"
import { useOverwritableTimeout } from "../useOverwritableTimeout"
import { GeneratorFormInput } from "./generatorArraySchema"
import { Generator } from "./generatorSchema"

export function ArrayEditorItem(
  { draggableProps, index, formData: { register, errors, remove, getValues, trigger } }: RowComponentProps<GeneratorFormInput>
): React.JSX.Element {
  const generatorName = getValues().generatorArray[index].name

  const setOverwritableTimeout = useOverwritableTimeout()
  const triggerWithDelay = useCallback(
    () => setOverwritableTimeout(async () => await trigger(), 1000),
    [setOverwritableTimeout, trigger]
  )

  return (
    <div data-testid="ArrayEditor-row">
      <Stack spacing={1}>
        <Stack direction="row" spacing={1}>
          <TextField
            label="generator"
            sx={{ width: 90 }} size="small"
            {...register(
              `generatorArray.${index}.name` as const,
              { onChange: triggerWithDelay }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "name" })}
            inputProps={{ "data-testid": "ArrayEditor-input-name" }}
          />
          <TextField
            label={`deg(${generatorName})`} type="number"
            sx={{ width: 80}} size="small"
            {...register(
              `generatorArray.${index}.degree` as const,
              {
                valueAsNumber: true,
                onChange: triggerWithDelay,
              }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "degree" }) || containsGlobalDegreeError({ errors })}
            inputProps={{ "data-testid": "ArrayEditor-input-degree" }}
          />
          <TextField
            label={`d(${generatorName})`}
            sx={{ width: 200 }} size="small"
            {...register(
              `generatorArray.${index}.differentialValue` as const,
              {
                onChange: triggerWithDelay,
              }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "differentialValue" })}
            inputProps={{ "data-testid": "ArrayEditor-input-differentialValue" }}
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
        <ShowErrorsAtIndex errors={errors} index={index}/>
      </Stack>
    </div>
  )
}

function ShowErrorsAtIndex({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}): React.JSX.Element {
  const error = errors.generatorArray?.[index]
  const fieldErrors = getFieldErrors({ error })
  return (
    <ShowFieldErrors fieldErrors={fieldErrors}/>
  )
}

function getFieldErrors(
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
