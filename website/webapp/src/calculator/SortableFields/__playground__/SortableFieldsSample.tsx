import React from "react"

import { zodResolver } from "@hookform/resolvers/zod"
import { useFieldArray, useForm } from "react-hook-form"

import { FormData, RowComponentProps, SortableFields } from ".."
import { PeopleFormInput, peopleFormValueSchema } from "./schema"

export function SortableFieldsSample(): React.JSX.Element {
  const defaultValues: PeopleFormInput = {
    personArray: [
      { name: "Bourbaki", age: 100 },
    ]
  }
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm({
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
      SortableFieldsSample
      <SortableFields
        RowComponent={PersonRow}
        Container={({ children }) => (<div>{children}</div>)}
        externalData={undefined}
        {...{ fields, move, formData }}
      />
      <button onClick={() => append({ name: "", age: 0 })}>
        Add row
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
      <span
        {...draggableProps}
        style={{
          backgroundColor: "gray",
          cursor: "grab",
          touchAction: "none",
        }}
      >
        move
      </span>
    </div>
  )
}
