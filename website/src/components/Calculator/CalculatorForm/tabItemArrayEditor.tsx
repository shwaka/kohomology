import { Alert, Button, Stack, TextField } from "@mui/material"
import React from "react"
import { DeepRequired, FieldArrayWithId, FieldErrorsImpl, useFieldArray, UseFieldArrayAppend, UseFieldArrayRemove, useForm, UseFormRegister } from "react-hook-form"
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
  const { handleSubmit, register, control, formState: { errors } } = useForm<GeneratorFormInput>({
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
    register, errors, fields, append, remove,
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
}

function ArrayEditor({ register, errors, fields, append, remove }: ArrayEditorProps): JSX.Element {
  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      {fields.map((field, index) => (
        <div key={field.id}>
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
              label="generator"
              sx={{ width: 200 }} size="small"
              {...register(`generatorArray.${index}.differentialValue` as const)}
            />
            <Button onClick={() => remove(index)}>
              Delete
            </Button>
          </Stack>
        </div>
      ))}
      {errors.generatorArray !== undefined && (
        <Alert severity="error">{errors.generatorArray.message}</Alert>
      )}
      <Button onClick={() => append({ name: "", degree: 0, differentialValue: "" })}>
        Add generator
      </Button>
    </Stack>
  )
}
