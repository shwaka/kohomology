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

export function validateGeneratorNames(generatorArray: Generator[]): true | string {
  const names = generatorArray.map((generator) => generator.name)
  const duplicatedNames = names.filter((item, index) => names.indexOf(item) !== index)
  if (duplicatedNames.length === 0) {
    return true
  }
  return "Generator names must be unique. Duplicated names are " + duplicatedNames.map((name) => `"${name}"`).join(", ")
}
