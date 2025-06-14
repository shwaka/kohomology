import React, { ReactNode } from "react"

import { OnSubmit } from "@calculator/EditorDialog"
import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { FormData, SortableFields } from "@calculator/SortableFields"
import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"

import { ArrayEditorItem } from "./ArrayEditorItem"
import { GeneratorFormInput, globalErrorsSchema } from "./generatorArraySchema"
import { Generator } from "./generatorSchema"

export interface ArrayEditorProps {
  register: UseFormRegister<GeneratorFormInput>
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
  fields: FieldArrayWithId<GeneratorFormInput, "generatorArray", "id">[]
  append: UseFieldArrayAppend<GeneratorFormInput, "generatorArray">
  remove: UseFieldArrayRemove
  move: UseFieldArrayMove
  getValues: UseFormGetValues<GeneratorFormInput>
  trigger: UseFormTrigger<GeneratorFormInput>
  onSubmit: OnSubmit
}

export function ArrayEditor({ register, errors, fields, append, remove, getValues, trigger, move, onSubmit }: ArrayEditorProps): React.JSX.Element {
  const onSubmitWithPreventDefault = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {
    event.preventDefault()
    await onSubmit(event)
  }
  const formData: FormData<GeneratorFormInput> = {
    register, remove, errors, getValues, trigger
  }
  // <button hidden type="submit"/> is necessary for onSubmit in form
  return (
    <form onSubmit={onSubmitWithPreventDefault}>
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

function getGlobalError(errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>): React.JSX.Element | undefined {
  const fieldErrorArray = getFieldErrorArray({ errors })
  return (
    <ShowFieldErrors fieldErrorArray={fieldErrorArray}/>
  )
}

function getFieldErrorArray(
  { errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }
): (FieldError | undefined)[] {
  const _global_errors = errors._global_errors
  if (_global_errors === undefined) {
    return []
  }
  const keys = Object.keys(globalErrorsSchema.shape) as (keyof typeof globalErrorsSchema.shape)[]
  return keys.map((key) => _global_errors[key] )
}

function SortableFieldsContainer({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}
