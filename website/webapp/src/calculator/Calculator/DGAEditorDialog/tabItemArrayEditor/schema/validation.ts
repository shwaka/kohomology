import { magicMessageToHideError } from "@calculator/ShowFieldErrors"
import { validateDifferentialValueOfTheLast } from "kohomology-js"

import { generatorArrayToJson } from "./ConvertGenerator"
import { Generator } from "./generatorSchema"

export function validateDifferentialValue(generatorArray: Generator[], index: number, value: string): true | string {
  if (generatorArray[index].differentialValue !== value) {
    throw new Error("generatorArray[index] and value do not match.")
  }
  const generatorsJson: string = generatorArrayToJson(generatorArray.slice(0, index + 1))
  const validationResult = validateDifferentialValueOfTheLast(generatorsJson)
  switch (validationResult.type) {
    case "success":
    case "N/A":
      return true
    default:
      return validationResult.message
  }
}

export function validateGeneratorDegrees(generatorArray: Generator[]): true | string {
  const positiveCount = generatorArray.filter((generator) => generator.degree > 0).length
  const negativeCount = generatorArray.filter((generator) => generator.degree < 0).length
  if (positiveCount > 0 && negativeCount > 0) {
    return "Cannot mix generators of positive and negative degrees."
  }
  return true
}

export function validateGeneratorNames(generatorArray: Generator[]): Map<number, string> {
  const names = generatorArray.map((generator) => generator.name)
  // const duplicatedNames = names.filter((item, index) => names.indexOf(item) !== index)
  const result: Map<number, string> = new Map()
  names.forEach((name, index) => {
    const firstOccurrence = names.indexOf(name)
    if (firstOccurrence !== index) {
      result.set(index, `Generator names must be unique, but ${name} is already used.`)
      result.set(firstOccurrence, magicMessageToHideError)
    }
  })
  return result
}
