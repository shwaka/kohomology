import { validateDifferentialValueOfTheLast, validateGeneratorName } from "kohomology-js"
import { z } from "zod"
import { generatorArrayToJson } from "./Generator"

const nameSchema = z.string().min(1, "Please enter the name.").superRefine((val: string, ctx) => {
  const validationResult = validateGeneratorName(val)
  switch (validationResult.type) {
    case "success":
      return
    case "error":
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: validationResult.message,
      })
      return
    default:
      throw new Error("This can't happen!")
  }
})

function numberSchemaWithRequiredError(message: string): z.ZodEffects<z.ZodNumber, number, number> {
  return z.preprocess(
    (val) => {
      if (val === "" || typeof val !== "number" || Number.isNaN(val)) {
        return undefined
      }
      return val
    },
    z.number({ required_error: message })
  ) as z.ZodEffects<z.ZodNumber, number, number>
}

const deegreeSchema = numberSchemaWithRequiredError("Please enter the degree.").refine(
  (val: number) => (val !== 0),
  "The degree cannot be 0."
)

// z.preprocess(
//   (val) => {
//     if (val === "" || typeof val !== "number" || Number.isNaN(val)) {
//       return undefined
//     }
//     return val
//   },
//   z.number({ required_error: "Please enter the degree." }).refine(
//     (val: number) => (val !== 0),
//     "The degree cannot be 0."
//   )
// ) as z.ZodEffects<z.ZodEffects<z.ZodNumber, number, number>, number, number>

const differentialValueSchema = z.string().min(1, "Please enter the value of the differential.")

export const generatorSchema = z.object({
  name: nameSchema,
  degree: deegreeSchema,
  differentialValue: differentialValueSchema,
})

export type Generator = z.infer<typeof generatorSchema>

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

function validateDifferentialValue(generatorArray: Generator[], index: number, value: string): true | string {
  if (generatorArray[index].differentialValue !== value) {
    throw new Error("generatorArray[index] and value do not match.")
  }
  const generatorsJson: string = generatorArrayToJson(generatorArray.slice(0, index + 1))
  const validationResult = validateDifferentialValueOfTheLast(generatorsJson)
  if (validationResult.type === "success") {
    return true
  } else {
    return validationResult.message
  }
}

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

function validateGeneratorDegrees(generatorArray: Generator[]): true | string {
  const positiveCount = generatorArray.filter((generator) => generator.degree > 0).length
  const negativeCount = generatorArray.filter((generator) => generator.degree < 0).length
  if (positiveCount > 0 && negativeCount > 0) {
    return "Cannot mix generators of positive and negative degrees."
  }
  return true
}

function validateGeneratorNames(generatorArray: Generator[]): true | string {
  const names = generatorArray.map((generator) => generator.name)
  const duplicatedNames = names.filter((item, index) => names.indexOf(item) !== index)
  if (duplicatedNames.length === 0) {
    return true
  }
  return "Generator names must be unique. Duplicated names are " + duplicatedNames.map((name) => `"${name}"`).join(", ")
}
