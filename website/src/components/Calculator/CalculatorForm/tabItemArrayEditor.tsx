import { Alert, Button, Stack, TextField } from "@mui/material"
import { validateDifferentialValue as validateDifferentialValueKt } from "kohomology-js"
import React from "react"
import { DeepRequired, FieldArrayWithId, FieldErrorsImpl, useFieldArray, UseFieldArrayAppend, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister } from "react-hook-form"
import { TabItem } from "../TabDialog"
import { generatorArrayToPrettyJson } from "./utils"

interface Generator {
  name: string
  degree: number
  differentialValue: string
}

interface GeneratorFormInput {
  generatorArray: Generator[]
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as [string, number, string][]
  return arr.map(([name, degree, differentialValue]) => ({ name, degree, differentialValue }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => [name, degree, differentialValue] as [string, number, string]
  )
  return generatorArrayToPrettyJson(arr)
}

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem<"array"> {
  const { handleSubmit, register, getValues, control, formState: { errors } } = useForm<GeneratorFormInput>({
    defaultValues: {
      generatorArray: jsonToGeneratorArray(args.json)
    }
  })
  const { fields, append, remove } = useFieldArray({
    control,
    name: "generatorArray",
  })
  function onSubmit(closeDialog: () => void): void {
    handleSubmit(
      ({generatorArray}) => {
        args.updateDgaWrapper(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )()
  }
  const arrayEditorProps: ArrayEditorProps = {
    register, errors, fields, append, remove, getValues,
  }
  return {
    tabKey: "array",
    label: "Array",
    onSubmit,
    render: () => (<ArrayEditor {...arrayEditorProps}/>),
  }
}

interface ArrayEditorProps {
  register: UseFormRegister<GeneratorFormInput>
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
  fields: FieldArrayWithId<GeneratorFormInput, "generatorArray", "id">[]
  append: UseFieldArrayAppend<GeneratorFormInput, "generatorArray">
  remove: UseFieldArrayRemove
  getValues: UseFormGetValues<GeneratorFormInput>
}

function validateDifferentialValue(generatorArray: Generator[], index: number, value: string): true | string {
  if (generatorArray[index].differentialValue !== value) {
    throw new Error("generatorArray[index] and value do not match.")
  }
  const generatorNames: string[] = generatorArray.map(({name}) => name).slice(0, index)
  const generatorDegrees: number[] = generatorArray.map(({degree}) => degree).slice(0, index)
  const degree: number = generatorArray[index].degree
  const validationResult = validateDifferentialValueKt(generatorNames, generatorDegrees, value, degree + 1)
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
    <Alert severity="error">
      {error.differentialValue !== undefined && error.differentialValue.message}
    </Alert>
  )
}

function ArrayEditor({ register, errors, fields, append, remove, getValues }: ArrayEditorProps): JSX.Element {
  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      {fields.map((field, index) => (
        <div key={field.id}>
          <Stack spacing={1}>
            <Stack direction="row" spacing={1}>
              <TextField
                label="generator"
                sx={{ width: 90 }} size="small"
                {...register(`generatorArray.${index}.name` as const)}
              />
              <TextField
                label="degree" type="number"
                sx={{ width: 80}} size="small"
                {...register(`generatorArray.${index}.degree` as const)}
              />
              <TextField
                label="differential"
                sx={{ width: 200 }} size="small"
                {...register(
                  `generatorArray.${index}.differentialValue` as const,
                  { validate: (value: string) => validateDifferentialValue(getValues().generatorArray, index, value) }
                )}
              />
              <Button onClick={() => remove(index)}>
                Delete
              </Button>
            </Stack>
            {getFieldError({ errors, index })}
          </Stack>
        </div>
      ))}
      <Button onClick={() => append({ name: "", degree: 0, differentialValue: "" })}>
        Add generator
      </Button>
    </Stack>
  )
}
