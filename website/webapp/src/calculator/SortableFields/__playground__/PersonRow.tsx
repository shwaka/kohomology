import { ReactElement } from "react"

import { DeepRequired, FieldErrorsImpl } from "react-hook-form"

import { RowComponentProps } from ".."
import { PeopleFormInput } from "./schema"

export function PersonRow({
  draggableProps, index, formData: { register, errors, remove, trigger },
}: RowComponentProps<PeopleFormInput>): ReactElement {
  return (
    <div>
      <input
        type="text"
        {...register(`personArray.${index}.name`)}
        onBlur={() => trigger() /* for global error */}
      />
      <input
        type="number"
        {...register(
          `personArray.${index}.age`,
          { valueAsNumber: true }
        )}
        onBlur={() => trigger() /* for global error */}
      />
      <button onClick={async () => {
        remove(index)
        await trigger() // for global error
      }}
      >
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
