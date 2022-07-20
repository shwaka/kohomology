import { Add, Delete } from "@mui/icons-material"
import { Alert, Button, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import { validateDifferentialValueOfTheLast } from "kohomology-js"
import React from "react"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, MultipleFieldErrors, useFieldArray, UseFieldArrayAppend, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { TabItem } from "../TabDialog"
import { generatorArrayToPrettyJson } from "./utils"

interface Generator {
  name: string
  degree: number
  differentialValue: string
}

interface GeneratorFormInput {
  dummy: "dummy"
  generatorArray: Generator[]
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as [string, number, string][]
  return arr.map(([name, degree, differentialValue]) => ({ name, degree, differentialValue }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => {
      return [name, isNaN(degree) ? 1 : degree, differentialValue] as [string, number, string]
    }
  )
  return generatorArrayToPrettyJson(arr)
}

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem<"array"> {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<GeneratorFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
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
    reset({ generatorArray })
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
    register, errors, fields, append, remove, getValues, trigger
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
  trigger: UseFormTrigger<GeneratorFormInput>
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

function PreserveNewline({ text }: { text: string }): JSX.Element {
  return (
    <React.Fragment>
      {text.split("\n").map((line, index) => <span key={index}>{line}<br/></span>)}
    </React.Fragment>
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
          <Alert severity="error" key={key}>
            <PreserveNewline text={errorForKey.message}/>
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

function validateGeneratorDegrees(generatorArray: Generator[]): true | string {
  const positiveCount = generatorArray.filter((generator) => generator.degree > 0).length
  const negativeCount = generatorArray.filter((generator) => generator.degree < 0).length
  if (positiveCount > 0 && negativeCount > 0) {
    return "Cannot mix generators of positive and negative degrees."
  }
  return true
}

function validateGeneratorNames(generatorArray: Generator[]): true | string {
  const names = generatorArray.map((generator) => generator.name)
  const duplicatedNames = names.filter((item, index) => names.indexOf(item) !== index)
  if (duplicatedNames.length === 0) {
    return true
  }
  return "Generator names must be unique. Duplicated names are " + duplicatedNames.map((name) => `"${name}"`).join(", ")
}

function getGlobalError(errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>): JSX.Element | undefined {
  const fieldError: FieldError | undefined = errors.dummy
  if (fieldError === undefined) {
    return undefined
  }
  const types: MultipleFieldErrors | undefined = fieldError.types
  if (types === undefined) {
    return undefined
  }
  return (
    <React.Fragment>
      {Object.entries(types).map(([errorType, message]) => (
        <Alert severity="error" key={errorType}>
          {message}
        </Alert>
      ))}
    </React.Fragment>
  )
}

function ArrayEditor({ register, errors, fields, append, remove, getValues, trigger }: ArrayEditorProps): JSX.Element {
  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      {fields.map((field, index) => {
        const generatorName = getValues().generatorArray[index].name
        return (
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
                  onBlur={() => trigger()}
                  error={containsError({ errors, index, key: "name" })}
                />
                <TextField
                  label={`deg(${generatorName})`} type="number"
                  sx={{ width: 80}} size="small"
                  {...register(
                    `generatorArray.${index}.degree` as const,
                    {
                      valueAsNumber: true,
                      required: "Please enter the degree.",
                      validate: (value: number) => value === 0 ? "The degree cannot be 0." : true
                    }
                  )}
                  onBlur={() => trigger()}
                  error={containsError({ errors, index, key: "degree" })}
                />
                <TextField
                  label={`d(${generatorName})`}
                  sx={{ width: 200 }} size="small"
                  {...register(
                    `generatorArray.${index}.differentialValue` as const,
                    {
                      validate: (value: string) =>
                        validateDifferentialValue(getValues().generatorArray, index, value),
                      required: "Please enter the value of the differential."
                    }
                  )}
                  onBlur={() => trigger()}
                  error={containsError({ errors, index, key: "differentialValue" })}
                />
                <Tooltip title="Delete this generator">
                  <IconButton onClick={() => remove(index)} size="small">
                    <Delete fontSize="small"/>
                  </IconButton>
                </Tooltip>
              </Stack>
              {getFieldError({ errors, index })}
            </Stack>
          </div>
      )})}
      <Button
        variant="outlined"
        onClick={() => append({ name: "", degree: 1, differentialValue: "0" })}
        startIcon={<Add/>}
        sx={{ textTransform: "none" }}
      >
        Add a generator
      </Button>
      <input
        hidden value="dummy"
        {...register("dummy", {
          validate: {
            positiveAndNegativeDegree: (_) => validateGeneratorDegrees(getValues().generatorArray),
            duplicatedNames: (_) => validateGeneratorNames(getValues().generatorArray),
          }
        })}
      />
      {getGlobalError(errors)}
    </Stack>
  )
}
