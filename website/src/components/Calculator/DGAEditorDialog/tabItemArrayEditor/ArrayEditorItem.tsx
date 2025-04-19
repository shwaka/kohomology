import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { Alert, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { useCallback } from "react"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"
import { useOverwritableTimeout } from "../useOverwritableTimeout"
import { GeneratorFormInput } from "./generatorArraySchema"
import { Generator } from "./generatorSchema"

export function ArrayEditorItem(
  { draggableProps, index, formData: { register, errors, remove, getValues, trigger } }: RowComponentProps<GeneratorFormInput>
): JSX.Element {
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
    </div>
  )
}

function getFieldError({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}): JSX.Element | undefined {
  const error = errors.generatorArray?.[index]
  if (error === undefined) {
    return undefined
  }
  return (
    <Stack spacing={0.3}>
      {(["name", "degree", "differentialValue"] as const).map((key) => {
        const errorForKey = error[key]
        if (errorForKey === undefined || errorForKey.message === undefined) {
          return undefined
        }
        return (
          <Alert severity="error" key={key} sx={{ whiteSpace: "pre-wrap" }}>
            {errorForKey.message}
          </Alert>
        )
      })}
    </Stack>
  )
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function containsGlobalDegreeError({ errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }): boolean {
  return (errors._global_errors?.generatorDegrees !== undefined)
}
