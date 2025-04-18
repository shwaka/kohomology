import { generatorArrayToPrettyJson } from "../../jsonUtils"
import { Generator, GeneratorFormInput } from "./generatorArraySchema"

export { Generator }

export { GeneratorFormInput }

export function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => {
      return [name, isNaN(degree) ? 1 : degree, differentialValue] as [string, number, string]
    }
  )
  return generatorArrayToPrettyJson(arr)
}

export function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as [string, number, string][]
  return arr.map(([name, degree, differentialValue]) => ({ name, degree, differentialValue }))
}
