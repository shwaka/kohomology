import { generatorArrayToPrettyJson } from "../../jsonUtils"

export interface Generator {
  name: string
  degree: number
  differentialValue: string
}

export interface GeneratorFormInput {
  dummy: "dummy"
  generatorArray: Generator[]
}

export function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => {
      return [name, isNaN(degree) ? 1 : degree, differentialValue] as [string, number, string]
    }
  )
  return generatorArrayToPrettyJson(arr)
}
