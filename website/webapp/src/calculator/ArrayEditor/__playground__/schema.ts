import { RefinementCtx, z } from "zod"

const indeterminateSchema = z.object({
  name: z.string().nonempty(),
  degree: z.number().positive(),
})

export type Indeterminate = z.infer<typeof indeterminateSchema>

const indeterminateArraySchema = z.array(indeterminateSchema)

export const indeterminateGlobalErrorsSchema = z.object({
  distinct: z.null(),
})

export const indeterminateFormValueSchema = z.object({
  indeterminateArray: indeterminateArraySchema,
  _global_errors: indeterminateGlobalErrorsSchema.optional(),
}).superRefine((val, ctx) => {
  addIssueForDistinctNames(val.indeterminateArray, ctx)
})

export type IndeterminateFormInput = z.infer<typeof indeterminateFormValueSchema>

function addIssueForDistinctNames(val: Indeterminate[], ctx: RefinementCtx): void {
  if (hasDuplicates(val.map((indeterminate) => indeterminate.name))) {
    ctx.addIssue({
      path: ["_global_errors", "distinct"],
      code: z.ZodIssueCode.custom,
      message: "Indeterminates must have distinct names."
    })
  }
}

function hasDuplicates<T>(array: T[]): boolean {
  return (new Set(array)).size !== array.length
}
