// DSV = Dot-Separated Values (immitating CSV = Comma-Separated Values)

export function jsonToDSV(json: string): string {
  const arrFromJson = JSON.parse(json) as [string, number, string][]
  return arrFromJson.map(
    ([name, degree, differentialValue]) => `${name}.${degree}.${differentialValue}`
  ).join(".")
}

export function dsvToJson(dsv: string): string {
  const arrFromDsv = dsv.split(".")
  const n = arrFromDsv.length / 3
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
