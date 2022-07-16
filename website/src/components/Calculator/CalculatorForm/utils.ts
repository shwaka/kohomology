export function compressJson(json: string): string | null {
  try {
    const obj = JSON.parse(json)
    return JSON.stringify(obj, null, undefined)
  } catch (e) {
    if (e instanceof SyntaxError) {
      return null
    } else {
      throw e
    }
  }
}

export function prettifyDGAJson(dgaJson: string): string {
  const generatorArray = JSON.parse(dgaJson) as [string, number, string][]
  return generatorArrayToPrettyJson(generatorArray)
}

export function generatorArrayToPrettyJson(generatorArray: [string, number, string][]): string {
  const arrayContent = generatorArray.map((generator) => {
    const name: string = generator[0]
    const degree: number = generator[1]
    const diff: string = generator[2]
    return `  ["${name}", ${degree}, "${diff}"]`
  }).join(",\n")
  return `[\n${arrayContent}\n]`
}
