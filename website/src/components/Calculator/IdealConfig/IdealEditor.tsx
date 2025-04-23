import { FormData, SortableFields } from "@components/SortableFields"
import { Add } from "@mui/icons-material"
import { Alert, Button, Stack } from "@mui/material"
import React, { ReactNode } from "react"
import { Control, DeepRequired, FieldError, FieldErrorsImpl, MultipleFieldErrors, useFieldArray, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { ExternalData, IdealEditorItem, IdealFormInput } from "./IdealEditorItem"

function SortableFieldsContainer({ children }: { children: ReactNode }): React.JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}

function getGlobalError(errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>): React.JSX.Element | undefined {
  if (errors.generatorArray !== undefined) {
    return undefined
  }
  // The following is the same as getGlobalError in tabItemArrayEditor.tsx
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

export interface IdealEditorProps {
  register: UseFormRegister<IdealFormInput>
  getValues: UseFormGetValues<IdealFormInput>
  errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>
  trigger: UseFormTrigger<IdealFormInput>
  control: Control<IdealFormInput>
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealEditor({ register, getValues, errors, trigger, control, validateGenerator, validateGeneratorArray }: IdealEditorProps): React.JSX.Element {
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })
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
        {getGlobalError(errors)}
      </Stack>
    </div>
  )
}
