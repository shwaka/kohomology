import { z } from "zod"

export function numberSchemaWithRequiredError(message: string): z.ZodEffects<z.ZodNumber, number, number> {
  return z.preprocess(
    (val) => {
      if (val === "" || typeof val !== "number" || Number.isNaN(val)) {
        return undefined
      }
      return val
    },
    z.number({ required_error: message })
  ) as z.ZodEffects<z.ZodNumber, number, number>
}
