// DSV = Dot-Separated Values (immitating CSV = Comma-Separated Values)

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

export function jsonToDSV(json: string): string | null {
  const arrFromJson: unknown = JSON.parse(json)
  if (!validateArrFromJson(arrFromJson)) {
    return null
  }
  return arrFromJson.map(
    ([name, degree, differentialValue]) => `${name}.${degree}.${differentialValue}`
  ).join(".")
}

export function dsvToJson(dsv: string): string {
  const arrFromDsv = dsv.split(".")
  const n = arrFromDsv.length / 3
  if (arrFromDsv.length % 3 !== 0) {
    throw new Error(`Invalid data from URL: "${arrFromDsv}"\nIts length must be divisible by 3, but it was ${arrFromDsv.length}`)
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
