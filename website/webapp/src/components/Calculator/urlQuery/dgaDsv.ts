import { DSV } from "./DSV"

function isTupleArray(value: unknown): value is [string, number, string][] {
  if (!Array.isArray(value)) {
    return false
  }
  for (const elm of value) {
    if ((!Array.isArray(elm)) ||
      (elm.length !== 3) ||
      (typeof elm[0] !== "string") ||
      (typeof elm[1] !== "number") ||
      (typeof elm[2] !== "string") ||
      // DSV cannot tread strings containing dots
      (elm[0].includes(".")) ||
      (elm[2].includes("."))
    ) {
      return false
    }
  }
  return true
}

function tupleArrayToStringArray(tupleArray: [string, number, string][]): string[] {
  return tupleArray.map(
    ([name, degree, differentialValue]) => [name, degree.toString(), differentialValue]
  ).flat()
}

export function dgaJsonToDsv(json: string): string | null {
  const arrFromJson: unknown = JSON.parse(json)
  if (!isTupleArray(arrFromJson)) {
    return null
  }
  const stringArray: string[] = tupleArrayToStringArray(arrFromJson)
  return DSV.stringify(stringArray)
}

function stringArrayToTupleArray(stringArray: string[]): [string, number, string][] {
  const n = stringArray.length / 3
  if (stringArray.length % 3 !== 0) {
    throw new Error(`Invalid data from URL: "${stringArray}"\nIts length must be divisible by 3, but was ${stringArray.length}`)
  }
  const result: [string, number, string][] = []
  for (let i = 0; i < n; i++) {
    result.push([
      stringArray[3 * i],
      parseInt(stringArray[3 * i + 1]),
      stringArray[3 * i + 2],
    ])
  }
  return result
}

export function dgaDsvToJson(dsv: string): string {
  const arrFromDsv: string[] = DSV.parse(dsv)
  const tupleArray = stringArrayToTupleArray(arrFromDsv)
  return JSON.stringify(tupleArray)
}
