import { z } from "zod"

export function getIdealGeneratorSchema(
  validateGenerator: (generator: string) => Promise<true | string>
): z.ZodEffects<z.ZodString, string, string> {
  return z.string().nonempty("Please enter the generator.").superRefine(async (val, ctx) => {
    const message: true | string = await validateGenerator(val)
    if (typeof message === "string") {
      ctx.addIssue({
        path: [],
        code: z.ZodIssueCode.custom,
        message,
      })
    }
  })
}

export const globalErrorsSchema = z.object({
  validateGeneratorArray: z.null(),
})

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
function getFormValueSchema(
  validateGenerator: (generator: string) => Promise<true | string>,
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>,
) {
  return z.object({
    generatorArray: getIdealGeneratorSchema(validateGenerator).array(),
    _global_errors: globalErrorsSchema.optional(),
  }).superRefine(async (val, ctx) => {
    const message: true | string = await validateGeneratorArray(val.generatorArray)
    if (typeof message === "string") {
      ctx.addIssue({
        path: ["_global_errors", "validateGeneratorArray"],
        code: z.ZodIssueCode.custom,
        message,
      })
    }
  })
}

export type IdealFormInput = z.infer<ReturnType<typeof getFormValueSchema>>
