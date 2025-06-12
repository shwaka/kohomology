import React from "react"

import { zodResolver } from "@hookform/resolvers/zod"
import { DeepRequired, FieldErrorsImpl, useFieldArray, useForm } from "react-hook-form"

import { FormData, RowComponentProps, SortableFields } from ".."
import { PeopleFormInput, peopleFormValueSchema, Person } from "./schema"

export function SortableFieldsSample(): React.JSX.Element {
  const defaultValues: PeopleFormInput = {
    personArray: [
      { name: "Bourbaki", age: 100 },
    ]
  }
  const { handleSubmit, register, getValues, trigger, control, formState: { errors } } = useForm({
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
      <button onClick={() => append({ name: "", age: 0 })}>
        Add row
      </button>
      <button
        onClick={handleSubmit(({ personArray }) => {
          window.alert(formatPersonArray(personArray))
        })}
      >
        Submit
      </button>
    </div>
  )
}

function PersonRow({
  draggableProps, index, formData: { register, errors, remove, getValues, trigger }
}: RowComponentProps<PeopleFormInput>): React.JSX.Element {
  return (
    <div>
      <input type="text" {...register(`personArray.${index}.name`)}/>
      <input type="number" {...register(`personArray.${index}.age`)}/>
      <button onClick={() => remove(index)}>
        Delete
      </button>
      <span
        {...draggableProps}
        style={{
          border: "1px solid gray",
          margin: "0 5px",
          cursor: "grab",
          touchAction: "none",
        }}
      >
        move
      </span>
      <span>
        {getErrorMessages(errors, index).map((message) => (
          <span
            key={index}
            style={{
              color: "red",
              border: "1px solid red",
              margin: "0 5px",
            }}
          >
            {message}
          </span>
        ))}
      </span>
    </div>
  )
}

function getErrorMessages(
  errors: FieldErrorsImpl<DeepRequired<PeopleFormInput>>,
  index: number,
): string[] {
  const error = errors.personArray?.[index]
  if (error === undefined) {
    return []
  }
  const result: string[] = []
  if (error.name?.message !== undefined) {
    result.push(error.name.message)
  }
  if (error.age?.message !== undefined) {
    result.push(error.age.message)
  }
  return result
}

function formatPersonArray(personArray: Person[]): string {
  return personArray
    .map((person) => `name: ${person.name}, age: ${person.age}`)
    .join("\n")
}
