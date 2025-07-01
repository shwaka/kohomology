import { z } from "zod"

export function getIdealGeneratorTextSchema(
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

export function getIdealGeneratorSchema(
  validateGenerator: (generator: string) => Promise<true | string>
): z.ZodType<{ text: string }> {
  return z.object({
    text: getIdealGeneratorTextSchema(validateGenerator)
  })
}

export const globalErrorsSchema = z.object({
  validateGeneratorArray: z.null(),
})

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
function getFormValueSchemaImpl(
  validateGenerator: (generator: string) => Promise<true | string>,
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>,
) {
  return z.object({
    generatorArray: getIdealGeneratorSchema(validateGenerator).array(),
    _global_errors: globalErrorsSchema.optional(),
  }).superRefine(async (val, ctx) => {
    const generatorTextArray = val.generatorArray.map((generator) => generator.text)
    const message: true | string = await validateGeneratorArray(generatorTextArray)
    if (typeof message === "string") {
      ctx.addIssue({
        path: ["_global_errors", "validateGeneratorArray"],
        code: z.ZodIssueCode.custom,
        message,
      })
    }
  })
}

export type Generator = z.infer<ReturnType<typeof getIdealGeneratorSchema>>
export type IdealFormInput = z.infer<ReturnType<typeof getFormValueSchemaImpl>>

export function getFormValueSchema(
  validateGenerator: (generator: string) => Promise<true | string>,
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>,
): z.ZodType<IdealFormInput> & { _def: { typeName: string } } {
  // See https://github.com/react-hook-form/resolvers/issues/782 for typeName in the return type
  return getFormValueSchemaImpl(validateGenerator, validateGeneratorArray)
}
