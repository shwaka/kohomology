import { validateGeneratorName } from "kohomology-js"
import { z } from "zod/v4"

import { numberSchemaWithRequiredError } from "./numberSchemaWithRequiredError"

export const nameSchema = z.string().min(1, "Please enter the name.").superRefine((val: string, ctx) => {
  const validationResult = validateGeneratorName(val)
  switch (validationResult.type) {
    case "success":
      return
    case "error":
      ctx.addIssue({
        code: "custom",
        message: validationResult.message,
      })
      return
    default:
      throw new Error("This can't happen!")
  }
})

export const deegreeSchema = numberSchemaWithRequiredError("Please enter the degree.").refine(
  (val: number) => (val !== 0),
  "The degree cannot be 0."
)

export const differentialValueSchema = z.string().min(1, "Please enter the value of the differential.")

export const generatorSchema = z.object({
  name: nameSchema,
  degree: deegreeSchema,
  differentialValue: differentialValueSchema,
})

export type Generator = z.infer<typeof generatorSchema>

export type GeneratorKey = keyof (typeof generatorSchema.shape)
