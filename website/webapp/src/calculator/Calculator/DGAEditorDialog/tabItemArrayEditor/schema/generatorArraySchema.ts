import { z } from "zod/v4"
import { $ZodRawIssue } from "zod/v4/core"

import { generatorSchema, Generator } from "./generatorSchema"
import { validateDifferentialValue, validateGeneratorDegrees, validateGeneratorNames } from "./validation"

export const generatorArraySchema = z.array(generatorSchema).check((ctx) => {
  addIssueForDifferentialValue(ctx.value, ctx.issues)
})

export const globalErrorsSchema = z.object({
  generatorDegrees: z.null(),
  // generatorNames: z.null(), // Previously this was used for addIssueForGeneratorNames
})

export const formValueSchema = z.object({
  generatorArray: generatorArraySchema,
  _global_errors: globalErrorsSchema.optional(),
}).check((ctx) => {
  addIssueForGeneratorDegrees(ctx.value.generatorArray, ctx.issues)
  addIssueForGeneratorNames(ctx.value.generatorArray, ctx.issues)
})

export type GeneratorFormInput = z.infer<typeof formValueSchema>

function addIssueForDifferentialValue(val: Generator[], issues: $ZodRawIssue[]): void {
  val.forEach((generator, index) => {
    const validationResult = validateDifferentialValue(val, index, generator.differentialValue)
    if (typeof validationResult === "string") {
      issues.push({
        input: val,
        path: [index, "differentialValue"],
        code: "custom",
        message: validationResult,
        // `continue: true` is necessary to run validation for formValueSchema after this
        continue: true,
      })
    }
  })
}

function addIssueForGeneratorDegrees(val: Generator[], issues: $ZodRawIssue[]): void {
  const validateDegreesResult = validateGeneratorDegrees(val)
  if (typeof validateDegreesResult === "string") {
    issues.push({
      input: val,
      path: ["_global_errors", "generatorDegrees"],
      code: "custom",
      message: validateDegreesResult,
    })
  }
}

function addIssueForGeneratorNames(val: Generator[], issues: $ZodRawIssue[]): void {
  const validateNamesResult: Map<number, string> = validateGeneratorNames(val)
  validateNamesResult.forEach((message, index) => {
    issues.push({
      input: val,
      path: ["generatorArray", index, "name"],
      code: "custom",
      message: message,
    })
  })
}
