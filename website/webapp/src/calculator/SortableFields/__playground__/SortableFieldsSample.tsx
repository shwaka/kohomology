import React, { ReactElement } from "react"

import { zodResolver } from "@hookform/resolvers/zod"
import { useFieldArray, useForm } from "react-hook-form"

import { FormData, SortableFields } from ".."
import { PersonRow } from "./PersonRow"
import { PeopleFormInput, peopleFormValueSchema, Person } from "./schema"

export function SortableFieldsSample(): ReactElement {
  const defaultValues: PeopleFormInput = {
    personArray: [
      { name: "Bourbaki", age: 100 },
    ]
  }
  const { handleSubmit, register, getValues, trigger, control, formState: { errors, isValid } } = useForm({
    mode: "onBlur",
    reValidateMode: "onBlur",
    defaultValues,
    resolver: zodResolver(peopleFormValueSchema),
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "personArray",
  })
  const formData: FormData<PeopleFormInput> = {
    register, remove, errors, getValues, trigger
  }
  return (
    <div>
      <SortableFields
        RowComponent={PersonRow}
        Container={({ children }) => (<div>{children}</div>)}
        externalData={undefined}
        {...{ fields, move, formData }}
      />
      {(errors._global_errors?.totalAge !== undefined) && (
        <div
          style={{
            color: "red",
            border: "1px solid red",
          }}
        >
          {errors._global_errors.totalAge.message}
        </div>
      )}
      <button onClick={async () => {
        append({ name: `person-${fields.length}`, age: 100 })
        await trigger() // for global error
      }}>
        Add row
      </button>
      <button
        onClick={handleSubmit(({ personArray }) => {
          window.alert(formatPersonArray(personArray))
        })}
        disabled={!isValid}
      >
        Submit
      </button>
    </div>
  )
}

function formatPersonArray(personArray: Person[]): string {
  return personArray
    .map((person) => `name: ${person.name}, age: ${person.age}`)
    .join("\n")
}
