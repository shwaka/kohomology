import { z } from "zod/v4"
import { $ZodRawIssue } from "zod/v4/core"

const indeterminateSchema = z.object({
  name: z.string().nonempty(),
  degree: z.number().positive(),
})

export type Indeterminate = z.infer<typeof indeterminateSchema>

export const indeterminateArraySchema = z.array(indeterminateSchema)

export const indeterminateGlobalErrorsSchema = z.object({
  distinct: z.null(),
})

export const indeterminateFormValueSchema = z.object({
  indeterminateArray: indeterminateArraySchema,
  _global_errors: indeterminateGlobalErrorsSchema.optional(),
}).check((ctx) => {
  addIssueForDistinctNames(ctx.value.indeterminateArray, ctx.issues)
})

export type IndeterminateFormInput = z.infer<typeof indeterminateFormValueSchema>

function addIssueForDistinctNames(val: Indeterminate[], issues: $ZodRawIssue[]): void {
  if (hasDuplicates(val.map((indeterminate) => indeterminate.name))) {
    issues.push({
      input: val,
      path: ["_global_errors", "distinct"],
      code: "custom",
      message: "Indeterminates must have distinct names.",
      continue: true,
    })
  }
}

function hasDuplicates<T>(array: T[]): boolean {
  return (new Set(array)).size !== array.length
}
