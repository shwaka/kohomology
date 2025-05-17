import { DSV } from "./DSV"

function isStringArray(value: unknown): value is string[] {
  if (!Array.isArray(value)) {
    return false
  }
  for (const elm of value) {
    if (typeof elm !== "string") {
      return false
    }
  }
  return true
}

export function idealJsonToDsv(json: string): string | null {
  const arrFromJson: unknown = JSON.parse(json)
  if (!isStringArray(arrFromJson)) {
    return null
  }
  return DSV.stringify(arrFromJson)
}

export function idealDsvToJson(dsv: string): string {
  const arrFromDsv: string[] = DSV.parse(dsv)
  return JSON.stringify(arrFromDsv)
}
