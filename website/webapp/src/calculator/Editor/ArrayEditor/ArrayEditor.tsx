import React, { ReactNode } from "react"

import { OnSubmit } from ".."
import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { FormData, RowComponentProps, SortableFields } from "@calculator/SortableFields"
import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import { ArrayPath, DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, FieldValues, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"

export interface ArrayEditorProps<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> {
  register: UseFormRegister<TFieldValues>
  errors: FieldErrorsImpl<DeepRequired<TFieldValues>>
  fields: FieldArrayWithId<TFieldValues, K, "id">[]
  append: UseFieldArrayAppend<TFieldValues, K>
  remove: UseFieldArrayRemove
  move: UseFieldArrayMove
  getValues: UseFormGetValues<TFieldValues>
  trigger: UseFormTrigger<TFieldValues>
  onSubmit: OnSubmit
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>) => (FieldError | undefined)[]
  getNext: (valueArray: TFieldValues[K][number][]) => TFieldValues[K][number]
  RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element
  arrayKey: K
}

export function ArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>({
  register, errors, fields, append, remove, getValues, trigger, move, onSubmit, getGlobalErrors, getNext, RowComponent, arrayKey,
}: ArrayEditorProps<TFieldValues, K>): React.JSX.Element {
  const onSubmitWithPreventDefault = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {
    event.preventDefault()
    await onSubmit(event)
  }
  const formData: FormData<TFieldValues> = {
    register, remove, errors, getValues, trigger
  }
  // <button hidden type="submit"/> is necessary for onSubmit in form
  return (
    <form onSubmit={onSubmitWithPreventDefault}>
      <Stack spacing={2} sx={{ marginTop: 1 }}>
        <SortableFields
          RowComponent={RowComponent}
          Container={SortableFieldsContainer}
          externalData={undefined}
          {...{ fields, move, formData }}
        />
        <Button
          variant="outlined"
          onClick={() => append(getNext(getValues()[arrayKey]))}
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
