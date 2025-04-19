import { RefinementCtx, z } from "zod"
import { generatorSchema, Generator } from "./generatorSchema"
import { validateDifferentialValue, validateGeneratorDegrees, validateGeneratorNames } from "./validation"

export const generatorArraySchema = z.array(generatorSchema).superRefine((val: Generator[], ctx) => {
  addIssueForDifferentialValue(val, ctx)
  addIssueForGeneratorDegrees(val, ctx)
  addIssueForGeneratorNames(val ,ctx)
})

export const formValueSchema = z.object({
  generatorArray: generatorArraySchema,
  dummy: z.literal("dummy"),
})

export type GeneratorFormInput = z.infer<typeof formValueSchema>

function addIssueForDifferentialValue(val: Generator[], ctx: RefinementCtx): void {
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
}

function addIssueForGeneratorDegrees(val: Generator[], ctx: RefinementCtx): void {
  const validateDegreesResult = validateGeneratorDegrees(val)
  if (typeof validateDegreesResult === "string") {
    ctx.addIssue({
      path: [],
      code: z.ZodIssueCode.custom,
      message: validateDegreesResult,
    })
  }
}

function addIssueForGeneratorNames(val: Generator[], ctx: RefinementCtx): void {
  const validateNamesResult = validateGeneratorNames(val)
  if (typeof validateNamesResult === "string") {
    ctx.addIssue({
      path: [],
      code: z.ZodIssueCode.custom,
      message: validateNamesResult,
    })
  }
}
