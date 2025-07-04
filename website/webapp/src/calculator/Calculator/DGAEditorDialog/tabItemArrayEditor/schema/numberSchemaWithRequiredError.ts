import { z } from "zod/v4"

export function numberSchemaWithRequiredError(message: string): z.ZodPipe<z.ZodTransform, z.ZodNumber> {
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
  )
}
