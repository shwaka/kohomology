import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { Alert, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import { validateDifferentialValueOfTheLast, validateGeneratorName } from "kohomology-js"
import React, { useCallback } from "react"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"
import { generatorArrayToPrettyJson } from "../../jsonUtils"
import { useOverwritableTimeout } from "../useOverwritableTimeout"

export interface Generator {
  name: string
  degree: number
  differentialValue: string
}

export interface GeneratorFormInput {
  dummy: "dummy"
  generatorArray: Generator[]
}

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
              {
                required: "Please enter the name.",
                validate: (value: string) => {
                  const validationResult = validateGeneratorName(value)
                  switch (validationResult.type) {
                    case "success":
                      return true
                    case "error":
                      return validationResult.message
                    default:
                      throw new Error("This can't happen!")
                  }
                },
                onChange: triggerWithDelay,
              }
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
                required: "Please enter the degree.",
                validate: (value: number) => value === 0 ? "The degree cannot be 0." : true,
                onChange: triggerWithDelay,
              }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "degree" })}
            inputProps={{ "data-testid": "ArrayEditor-input-degree" }}
          />
          <TextField
            label={`d(${generatorName})`}
            sx={{ width: 200 }} size="small"
            {...register(
              `generatorArray.${index}.differentialValue` as const,
              {
                validate: (value: string) =>
                  validateDifferentialValue(getValues().generatorArray, index, value),
                required: "Please enter the value of the differential.",
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

export function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => {
      return [name, isNaN(degree) ? 1 : degree, differentialValue] as [string, number, string]
    }
  )
  return generatorArrayToPrettyJson(arr)
}

function validateDifferentialValue(generatorArray: Generator[], index: number, value: string): true | string {
  if (generatorArray[index].differentialValue !== value) {
    throw new Error("generatorArray[index] and value do not match.")
  }
  const generatorsJson: string = generatorArrayToJson(generatorArray.slice(0, index + 1))
  const validationResult = validateDifferentialValueOfTheLast(generatorsJson)
  if (validationResult.type === "success") {
    return true
  } else {
    return validationResult.message
  }
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
