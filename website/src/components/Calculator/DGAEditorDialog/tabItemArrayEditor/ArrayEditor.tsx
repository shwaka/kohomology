import { FormData, SortableFields } from "@components/SortableFields"
import { Add } from "@mui/icons-material"
import { Alert, Button, Stack } from "@mui/material"
import React, { ReactNode } from "react"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, MultipleFieldErrors, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { ArrayEditorItem } from "./ArrayEditorItem"
import { Generator, GeneratorFormInput } from "./Generator"

export interface ArrayEditorProps {
  register: UseFormRegister<GeneratorFormInput>
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
  fields: FieldArrayWithId<GeneratorFormInput, "generatorArray", "id">[]
  append: UseFieldArrayAppend<GeneratorFormInput, "generatorArray">
  remove: UseFieldArrayRemove
  getValues: UseFormGetValues<GeneratorFormInput>
  trigger: UseFormTrigger<GeneratorFormInput>
  move: UseFieldArrayMove
  submit: () => void
}

export function ArrayEditor({ register, errors, fields, append, remove, getValues, trigger, move, submit }: ArrayEditorProps): JSX.Element {
  const onSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault()
    submit()
  }
  const formData: FormData<GeneratorFormInput> = {
    register, remove, errors, getValues, trigger
  }
  // <button hidden type="submit"/> is necessary for onSubmit in form
  return (
    <form onSubmit={onSubmit}>
      <Stack spacing={2} sx={{ marginTop: 1 }}>
        <SortableFields
          RowComponent={ArrayEditorItem}
          Container={SortableFieldsContainer}
          externalData={undefined}
          {...{ fields, move, formData }}
        />
        <Button
          variant="outlined"
          onClick={() => append({
            name: getNameOfNextGenerator(getValues().generatorArray),
            degree: 1,
            differentialValue: "0"
          })}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <input
          hidden value="dummy"
          {...register("dummy")}
        />
        {getGlobalError(errors)}
      </Stack>
      <button hidden type="submit"/>
    </form>
  )
}

function getNameOfNextGenerator(generatorArray: Generator[]): string {
  const existingNames: string[] = generatorArray.map((generator) => generator.name)
  // "d" cannot be used since it represents the differential
  const nameCandidates: string[] = "xyzuvwabc".split("")
  for (const candidate of nameCandidates) {
    if (!existingNames.includes(candidate)) {
      return candidate
    }
  }
  return ""
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

function SortableFieldsContainer({ children }: { children: ReactNode }): JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}
