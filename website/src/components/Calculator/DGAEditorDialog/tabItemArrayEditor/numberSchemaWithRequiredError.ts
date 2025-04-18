import { z } from "zod"

export function numberSchemaWithRequiredError(message: string): z.ZodEffects<z.ZodNumber, number, number> {
  return z.preprocess(
    (val) => {
      if (Number.isNaN(val)) {
        return undefined
      }
      return val
    },
    z.number({ required_error: message })
  ) as z.ZodEffects<z.ZodNumber, number, number>
}
