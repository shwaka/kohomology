import { z } from "zod/v4"

export function getIdealGeneratorTextSchema(
  validateGenerator: (generator: string) => Promise<true | string>
): z.ZodString {
  return z.string().nonempty("Please enter the generator.").check(async (ctx) => {
    const message: true | string = await validateGenerator(ctx.value)
    if (typeof message === "string") {
      ctx.issues.push({
        input: ctx.value,
        path: [],
        code: "custom",
        message,
      })
    }
  })
}

export function getIdealGeneratorSchema(
  validateGenerator: (generator: string) => Promise<true | string>
): z.ZodType<{ text: string }, { text: string }> {
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
  }).check(async (ctx) => {
    const generatorTextArray = ctx.value.generatorArray.map((generator) => generator.text)
    const message: true | string = await validateGeneratorArray(generatorTextArray)
    if (typeof message === "string") {
      ctx.issues.push({
        input: ctx.value,
        path: ["_global_errors", "validateGeneratorArray"],
        code: "custom",
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
): z.ZodType<IdealFormInput, IdealFormInput> {
  return getFormValueSchemaImpl(validateGenerator, validateGeneratorArray)
}
