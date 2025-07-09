import { ReactElement } from "react"

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod/v4"

import { numberSchemaWithRequiredError } from "../numberSchemaWithRequiredError"

const errorMessage = "Please enter the value!!!"

const schema = z.object({
  number: z.number(),
  numberSchemaWithRequiredError: numberSchemaWithRequiredError(errorMessage),
})

type FormData = z.infer<typeof schema>

const keys = ["number", "numberSchemaWithRequiredError"] as const

function NumberSchemaSampleImpl(
  { valueAsNumber }: { valueAsNumber: boolean },
): ReactElement {
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    mode: "onChange",
    reValidateMode: "onChange",
  })

  return (
    <div>
      <h3>valueAsNumber: {String(valueAsNumber)}</h3>
      <ul>
        {keys.map((key) => (
          <li key={key}>
            <span>{key}</span>
            <input
              type="number"
              {...register(key, { valueAsNumber })}
            />
            {getValues()[key]} ({typeof getValues()[key]})
            {errors[key] && (
              <span
                style={{ color: "red" }}
              >
                {errors[key].message}
              </span>
            )}
          </li>
        ))}
      </ul>
      <button onClick={handleSubmit(() => { console.log("Submit success") })}>
        Submit (to validate)
      </button>
    </div>
  )
}

export function NumberSchemaSample(): ReactElement {
  return (
    <div>
      <NumberSchemaSampleImpl valueAsNumber={true} />
      <NumberSchemaSampleImpl valueAsNumber={false} />
    </div>
  )
}
