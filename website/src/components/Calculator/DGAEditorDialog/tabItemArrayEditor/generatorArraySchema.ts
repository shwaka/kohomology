import { RefinementCtx, z } from "zod"
import { generatorSchema, Generator } from "./generatorSchema"
import { validateDifferentialValue, validateGeneratorDegrees, validateGeneratorNames } from "./validation"

export const generatorArraySchema = z.array(generatorSchema).superRefine((val, ctx) => {
  addIssueForDifferentialValue(val, ctx)
})

export const formValueSchema = z.object({
  generatorArray: generatorArraySchema,
  _global_errors: z.object({
    generatorDegrees: z.null(),
    generatorNames: z.null(),
  }).optional(),
}).superRefine((val, ctx) => {
  addIssueForGeneratorDegrees(val.generatorArray, ctx)
  addIssueForGeneratorNames(val.generatorArray, ctx)
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
      path: ["_global_errors", "generatorDegrees"],
      code: z.ZodIssueCode.custom,
      message: validateDegreesResult,
    })
  }
}

function addIssueForGeneratorNames(val: Generator[], ctx: RefinementCtx): void {
  const validateNamesResult: Map<number, string> = validateGeneratorNames(val)
  validateNamesResult.forEach((message, index) => {
    console.log(`add ${index}: ${message}`)
    ctx.addIssue({
      path: ["generatorArray", index, "name"],
      code: z.ZodIssueCode.custom,
      message: message,
    })
  })
}
