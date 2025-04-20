import { RowComponentProps } from "@components/SortableFields"
import { Delete, DragHandle } from "@mui/icons-material"
import { Alert, Collapse, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { useCallback } from "react"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"
import { useOverwritableTimeout } from "../useOverwritableTimeout"
import { GeneratorFormInput } from "./generatorArraySchema"
import { Generator, GeneratorKey } from "./generatorSchema"
import { magicMessageToHideError } from "./validation"
import { motion, AnimatePresence } from "motion/react"

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
        <ShowErrorsAtIndex errors={errors} index={index}/>
      </Stack>
    </div>
  )
}

function ShowErrorsAtIndex({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}): React.JSX.Element {
  const error = errors.generatorArray?.[index]
  return (
    <ShowError error={error}/>
  )
}

function ShowError({ error }: { error: FieldErrorsImpl<Generator> | undefined }): React.JSX.Element {
  return (
    <Stack spacing={0.3}>
      <AnimatePresence mode="sync">
        {(["name", "degree", "differentialValue"] as const).map((key) => {
          const message: string | undefined = getMessage({ error, key })
          if (message === undefined) {
            return undefined
          }
          return (
            <motion.div
              key={key}
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.3 }}
              style={{ overflow: "hidden", color: "red", marginTop: 4 }}
            >
              <Alert
                severity="error"
                sx={{ whiteSpace: "pre-wrap" }}
              >
                {message}
              </Alert>
            </motion.div>
          )
        })}
      </AnimatePresence>
    </Stack>
  )
}

function getMessage(
  { error, key }: { error: FieldErrorsImpl<Generator> | undefined, key: GeneratorKey }
): string | undefined {
  if (error === undefined) {
    return undefined
  }
  const errorForKey = error[key]
  if (errorForKey === undefined || errorForKey.message === undefined) {
    return undefined
  }
  const message = errorForKey.message
  if (message === magicMessageToHideError) {
    return undefined
  }
  return message
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function containsGlobalDegreeError({ errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }): boolean {
  return (errors._global_errors?.generatorDegrees !== undefined)
}
