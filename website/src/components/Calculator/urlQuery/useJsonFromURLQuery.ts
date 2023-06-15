import { prettifyDGAJson } from "../jsonUtils"
import { URLQueryResult } from "./URLQueryResult"
import { dsvToJson } from "./dotSeparatedValues"
import { useURLSearchParams } from "./useURLSearchParams"
import { ParamName } from "./ParamName"

function useDgaJsonFromURLQuery(): string | null {
  const urlSearchParams = useURLSearchParams()
  const dgaJson: string | null = urlSearchParams.get(ParamName.dgaJson)
  if (dgaJson !== null) {
    return dgaJson
  }
  const dgaDsv: string | null = urlSearchParams.get(ParamName.dgaDsv)
  if (dgaDsv !== null) {
    return dsvToJson(dgaDsv)
  }
  return null
}

export function useJsonFromURLQuery(): URLQueryResult<string> {
  const dgaJson: string | null = useDgaJsonFromURLQuery()
  if (dgaJson === null) {
    return {
      type: "unspecified",
    }
  }
  try {
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
    } else {
      console.error(e)
      return {
        type: "error",
        message: "[Error] Some error occurred while parsing URL parameter"
      }
    }
  }
}
