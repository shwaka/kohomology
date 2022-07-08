import { useLocation } from "@docusaurus/router"
import { useMemo } from "react"
import { sphere } from "./examples"

function useQuery(): URLSearchParams {
  // https://v5.reactrouter.com/web/example/query-parameters
  const { search } = useLocation()
  return useMemo(
    () => new URLSearchParams(search),
    [search]
  )
}

function compressJson(json: string): string | null {
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

// function prettifyJson(json: string): string {
//   const obj = JSON.parse(json)
//   return JSON.stringify(obj, null, 2)
// }

function prettifyDGAJson(dgaJson: string): string {
  const generatorArray = JSON.parse(dgaJson)
  const arrayContent = generatorArray.map((generator) => {
    const name: string = generator[0]
    const degree: number = generator[1]
    const diff: string = generator[2]
    return `  ["${name}", ${degree}, "${diff}"]`
  }).join(",\n")
  return `[\n${arrayContent}\n]`
}

interface CreateURLSearchParamsArgs {
  dgaJson: string
}

export function createURLSearchParams(
  { dgaJson }: CreateURLSearchParamsArgs
): URLSearchParams | null {
  const urlSearchParams = new URLSearchParams()
  const compressedJson: string | null = compressJson(dgaJson)
  if (compressedJson === null) {
    return null
  }
  urlSearchParams.append("dgaJson", compressedJson)
  return urlSearchParams
}

export function useDefaultDGAJson(): string {
  const urlSearchParams = useQuery()
  const defaultJson = sphere(2)
  try {
    const dgaJson: string | null = urlSearchParams.get("dgaJson")
    return (dgaJson !== null) ? prettifyDGAJson(dgaJson) : defaultJson
  } catch (e) {
    if (e instanceof SyntaxError) {
      console.log("[Error] Invalid JSON is given as URL parameter.")
      console.log(e)
      return defaultJson
    } else {
      throw e
    }
  }
}
