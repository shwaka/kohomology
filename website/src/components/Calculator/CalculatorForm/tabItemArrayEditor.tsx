import { Add, Delete } from "@mui/icons-material"
import { Alert, Button, IconButton, Stack, TextField, Tooltip } from "@mui/material"
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
  const { handleSubmit, register, getValues, setValue, clearErrors, control, formState: { errors } } = useForm<GeneratorFormInput>({
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
  function beforeOpen(): void {
    const generatorArray = jsonToGeneratorArray(args.json)
    setValue("generatorArray", generatorArray)
    clearErrors()
  }
  function preventQuit(): string | undefined {
    const generatorArray = getValues().generatorArray
    if (generatorArrayToJson(generatorArray) !== args.json) {
      return "Your input is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  const arrayEditorProps: ArrayEditorProps = {
    register, errors, fields, append, remove, getValues,
  }
  return {
    tabKey: "array",
    label: "Array",
    onSubmit,
    beforeOpen,
    preventQuit,
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
    <Stack spacing={0.3}>
      {(["name", "degree", "differentialValue"] as const).map((key) => {
        const errorForKey = error[key]
        if (errorForKey === undefined) {
          return undefined
        }
        return (
          <Alert severity="error" key={key}>
            {errorForKey.message}
          </Alert>
        )
      })}
    </Stack>
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
                {...register(
                  `generatorArray.${index}.name` as const,
                  { required: "Please enter the name."}
                )}
              />
              <TextField
                label="degree" type="number"
                sx={{ width: 80}} size="small"
                {...register(
                  `generatorArray.${index}.degree` as const,
                  {
                    valueAsNumber: true,
                    required: "Please enter the degree.",
                    validate: (value: number) => value === 0 ? "The degree cannot be 0." : true
                  }
                )}
              />
              <TextField
                label="differential"
                sx={{ width: 200 }} size="small"
                {...register(
                  `generatorArray.${index}.differentialValue` as const,
                  {
                    validate: (value: string) =>
                      validateDifferentialValue(getValues().generatorArray, index, value),
                    required: "Please enter the value of the differential."
                  }
                )}
              />
              <Tooltip title="Delete this generator">
                <IconButton onClick={() => remove(index)}>
                  <Delete/>
                </IconButton>
              </Tooltip>
            </Stack>
            {getFieldError({ errors, index })}
          </Stack>
        </div>
      ))}
      <Button
        variant="outlined"
        onClick={() => append({ name: "", degree: 1, differentialValue: "0" })}
        startIcon={<Add/>}
        sx={{ textTransform: "none" }}
      >
        Add a generator
      </Button>
    </Stack>
  )
}
