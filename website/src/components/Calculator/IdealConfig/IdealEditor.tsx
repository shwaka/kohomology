import { FormData, SortableFields } from "@components/SortableFields"
import { Add } from "@mui/icons-material"
import { Alert, Button, Stack } from "@mui/material"
import React, { ReactNode } from "react"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, MultipleFieldErrors, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { ExternalData, IdealEditorItem, IdealFormInput } from "./IdealEditorItem"
import { ShowFieldErrors } from "@components/ShowFieldErrors"

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
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealEditor({ register, getValues, errors, trigger, fields, append, remove, move, validateGenerator, validateGeneratorArray }: IdealEditorProps): React.JSX.Element {
  const formData: FormData<IdealFormInput> = {
    register, remove, errors, getValues, trigger,
  }

  const externalData: ExternalData = { validateGenerator }

  return (
    <div>
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
        <input
          hidden value="dummy"
          {...register("dummy", {
            validate: (_) => {
              const generatorArray = getValues().generatorArray.map((generator) => generator.text)
              return validateGeneratorArray(generatorArray)
            },
          })}
        />
        <ShowFieldErrors fieldErrorArray={getFieldErrorArray(errors)}/>
      </Stack>
    </div>
  )
}

function getFieldErrorArray(errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>): (FieldError | undefined)[] {
  if (errors.generatorArray !== undefined) {
    return []
  }
  const fieldError: FieldError | undefined = errors.dummy
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
