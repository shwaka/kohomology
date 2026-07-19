import { type ReactNode, type ReactElement, type FormEvent } from "react"

import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { type FormData, type RowComponentProps, SortableFields } from "@calculator/SortableFields"
import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import { type ArrayPath, type DeepRequired, type FieldArrayWithId, type FieldError, type FieldErrorsImpl, type FieldValues, type UseFieldArrayAppend, type UseFieldArrayMove, type UseFieldArrayRemove, type UseFormGetValues, type UseFormRegister, type UseFormTrigger } from "react-hook-form"

import { type OnSubmit } from ".."

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
  RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => ReactElement
  arrayKey: K
}

export function ArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>({
  register, errors, fields, append, remove, getValues, trigger, move, onSubmit, getGlobalErrors, getNext, RowComponent, arrayKey,
}: ArrayEditorProps<TFieldValues, K>): ReactElement {
  const onSubmitWithPreventDefault = async (event: FormEvent<HTMLFormElement>): Promise<void> => {
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
          startIcon={<Add />}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <ShowFieldErrors fieldErrorArray={getGlobalErrors(errors)} />
      </Stack>
      <button hidden type="submit" />
    </form>
  )
}

function SortableFieldsContainer({ children }: { children: ReactNode }): ReactElement {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}
