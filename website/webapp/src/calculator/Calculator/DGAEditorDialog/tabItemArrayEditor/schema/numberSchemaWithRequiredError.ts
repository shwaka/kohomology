import { z } from "zod/v4"

// numberSchemaWithRequiredError is intended to be used with react-hook-form.
// Consider an input tag with register(key, { valueAsNumber: true }).
// Its value is NaN if the input is empty.
// z.number().parse(NaN) throws an error, but its message cannot be customized.
// numberSchemaWithRequiredError throws an error with a custom error message.
export function numberSchemaWithRequiredError(message: string): z.ZodPipe<z.ZodTransform<number, number>, z.ZodNumber> {
  return z.preprocess(
    (val) => {
      if (Number.isNaN(val)) {
        return undefined
      }
      return val
    },
    z.number({
      error: (issue) => (issue.input === undefined) ? message : undefined
    })
  ) as z.ZodPipe<z.ZodTransform<number, number>, z.ZodNumber>
}
