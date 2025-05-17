import { OnSubmit } from "@calculator/EditorDialog"
import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { FormData, SortableFields } from "@calculator/SortableFields"
import { Add } from "@mui/icons-material"
import { Button, Stack } from "@mui/material"
import React, { ReactNode } from "react"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { ExternalData, IdealEditorItem } from "./IdealEditorItem"
import { IdealFormInput } from "./schema"

function SortableFieldsContainer({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}

export interface IdealEditorProps {
  register: UseFormRegister<IdealFormInput>
  getValues: UseFormGetValues<IdealFormInput>
  errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>
  trigger: UseFormTrigger<IdealFormInput>
  fields: FieldArrayWithId<IdealFormInput, "generatorArray", "id">[]
  append: UseFieldArrayAppend<IdealFormInput, "generatorArray">
  remove: UseFieldArrayRemove
  move: UseFieldArrayMove
  onSubmit: OnSubmit
}

export function IdealEditor({ register, getValues, errors, trigger, fields, append, remove, move, onSubmit }: IdealEditorProps): React.JSX.Element {
  const onSubmitWithPreventDefault = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {
    event.preventDefault()
    await onSubmit(event)
  }
  const formData: FormData<IdealFormInput> = {
    register, remove, errors, getValues, trigger,
  }

  const externalData: ExternalData = {}

  // <button hidden type="submit"/> is necessary for onSubmit in form
  return (
    <form onSubmit={onSubmitWithPreventDefault}>
      <Stack spacing={2} sx={{ marginTop: 1 }}>
        <SortableFields
          RowComponent={IdealEditorItem}
          Container={SortableFieldsContainer}
          {...{ fields, move, formData, externalData }}
        />
        <Button
          variant="outlined"
          onClick={() => append({ text: "" })}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <ShowFieldErrors fieldErrorArray={getFieldErrorArray(errors)}/>
      </Stack>
      <button hidden type="submit"/>
    </form>
  )
}

function getFieldErrorArray(errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>): (FieldError | undefined)[] {
  if (errors.generatorArray !== undefined) {
    return []
  }
  const fieldError: FieldError | undefined = errors._global_errors?.validateGeneratorArray
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
