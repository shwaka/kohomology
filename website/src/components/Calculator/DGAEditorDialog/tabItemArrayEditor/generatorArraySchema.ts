import { z } from "zod"
import { generatorSchema, Generator } from "./generatorSchema"
import { validateDifferentialValue, validateGeneratorDegrees, validateGeneratorNames } from "./validation"

export const generatorArraySchema = z.array(generatorSchema).superRefine((val: Generator[], ctx) => {
  val.forEach((generator, index) => {
    const validationResult = validateDifferentialValue(val, index, generator.differentialValue)
    if (typeof validationResult === "string") {
      ctx.addIssue({
        path: [index, "differentialValue"],
        code: z.ZodIssueCode.custom,
        message: validationResult,
      })
    }
  })
})

export const formValueSchema = z.object({
  generatorArray: generatorArraySchema,
  dummy: z.literal("dummy"),
}).superRefine((val, ctx) => {
  const validateDegreesResult = validateGeneratorDegrees(val.generatorArray)
  if (typeof validateDegreesResult === "string") {
    ctx.addIssue({
      path: ["dummy"],
      code: z.ZodIssueCode.custom,
      message: validateDegreesResult,
    })
  }
  const validateNamesResult = validateGeneratorNames(val.generatorArray)
  if (typeof validateNamesResult === "string") {
    ctx.addIssue({
      path: ["dummy"],
      code: z.ZodIssueCode.custom,
      message: validateNamesResult,
    })
  }
})

export type GeneratorFormInput = z.infer<typeof formValueSchema>
