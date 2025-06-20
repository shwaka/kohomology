import React, { ReactNode } from "react"

import { OnSubmit } from "@calculator/EditorDialog"
import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { FormData, SortableFields } from "@calculator/SortableFields"
import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"

import { ArrayEditorItem } from "./ArrayEditorItem"
import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

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
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>) => (FieldError | undefined)[]
  getNext: (generatorArray: Generator[]) => Generator
}

export function ArrayEditor({ register, errors, fields, append, remove, getValues, trigger, move, onSubmit, getGlobalErrors, getNext }: ArrayEditorProps): React.JSX.Element {
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
          onClick={() => append(getNext(getValues().generatorArray))}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <ShowFieldErrors fieldErrorArray={getGlobalErrors(errors)}/>
      </Stack>
      <button hidden type="submit"/>
    </form>
  )
}

function SortableFieldsContainer({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}
