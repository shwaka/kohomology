import { prettifyDGAJson } from "../jsonUtils"
import { ParamName } from "./ParamName"
import { URLQueryResult } from "./URLQueryResult"
import { dgaDsvToJson } from "./dgaDsv"
import { useURLSearchParams } from "./useURLSearchParams"

function getDgaJsonFromURLQuery(urlSearchParams: URLSearchParams): string | null {
  const dgaJson: string | null = urlSearchParams.get(ParamName.dgaJson)
  if (dgaJson !== null) {
    return dgaJson
  }
  const dgaDsv: string | null = urlSearchParams.get(ParamName.dgaDsv)
  if (dgaDsv !== null) {
    return dgaDsvToJson(dgaDsv)
  }
  return null
}

export function useJsonFromURLQuery(): URLQueryResult<string> {
  const urlSearchParams = useURLSearchParams()
  try {
    const dgaJson: string | null = getDgaJsonFromURLQuery(urlSearchParams)
    if (dgaJson === null) {
      return {
        type: "unspecified",
      }
    }
    return {
      type: "success",
      value: prettifyDGAJson(dgaJson),
    }
  } catch (e) {
    if (e instanceof SyntaxError) {
      return {
        type: "error",
        message: `[Error] Invalid JSON is given as URL parameter.\n${e.message}`
      }
    } else if (e instanceof Error){
      return {
        type: "error",
        message: e.message,
      }
    } else {
      console.error(e)
      return {
        type: "error",
        message: "[Error] Some error occurred while parsing URL parameter"
      }
    }
  }
}
