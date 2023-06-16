// DSV = Dot-Separated Values (immitating CSV = Comma-Separated Values)

// Similar API to JSON (JSON.parse and JSON.stringify)
export const DSV = {
  parse: (dsv: string): string[] => dsv.split("."),
  stringify: (value: string[]): string => value.join(".")
}

function validateArrFromJson(arrFromJson: unknown): arrFromJson is [string, number, string][] {
  if (!Array.isArray(arrFromJson)) {
    return false
  }
  for (const elm of arrFromJson) {
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

export function jsonToDsv(json: string): string | null {
  const arrFromJson: unknown = JSON.parse(json)
  if (!validateArrFromJson(arrFromJson)) {
    return null
  }
  const stringArray: string[] = arrFromJson.map(
    ([name, degree, differentialValue]) => [name, degree.toString(), differentialValue]
  ).flat()
  return DSV.stringify(stringArray)
}

export function dsvToJson(dsv: string): string {
  const arrFromDsv = DSV.parse(dsv)
  const n = arrFromDsv.length / 3
  if (arrFromDsv.length % 3 !== 0) {
    throw new Error(`Invalid data from URL: "${arrFromDsv}"\nIts length must be divisible by 3, but was ${arrFromDsv.length}`)
  }
  const result: [string, number, string][] = []
  for (let i = 0; i < n; i++) {
    result.push([
      arrFromDsv[3 * i],
      parseInt(arrFromDsv[3 * i + 1]),
      arrFromDsv[3 * i + 2],
    ])
  }
  return JSON.stringify(result)
}
