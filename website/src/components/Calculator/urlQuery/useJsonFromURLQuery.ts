import { prettifyDGAJson } from "../jsonUtils"
import { dsvToJson } from "./dotSeparatedValues"
import { useURLSearchParams } from "./useURLSearchParams"

function useDgaJsonFromURLQuery(): string | null {
  const urlSearchParams = useURLSearchParams()
  const dgaJson: string | null = urlSearchParams.get("dgaJson")
  if (dgaJson !== null) {
    return dgaJson
  }
  const dgaDsv: string | null = urlSearchParams.get("dgaDsv")
  if (dgaDsv !== null) {
    return dsvToJson(dgaDsv)
  }
  return null
}

interface QueryResultSuccess {
  type: "success"
  json: string
}

interface QueryResultUnspecified {
  type: "unspecified"
}

interface QueryResultParseError {
  type: "parseError"
  errorMessage: string
}

export type QueryResult = QueryResultSuccess | QueryResultUnspecified | QueryResultParseError

export function useJsonFromURLQuery(): QueryResult {
  const dgaJson: string | null = useDgaJsonFromURLQuery()
  if (dgaJson === null) {
    return {
      type: "unspecified",
    }
  }
  try {
    return {
      type: "success",
      json: prettifyDGAJson(dgaJson),
    }
  } catch (e) {
    if (e instanceof SyntaxError) {
      return {
        type: "parseError",
        errorMessage: `[Error] Invalid JSON is given as URL parameter.\n${e.message}`
      }
    } else {
      console.error(e)
      return {
        type: "parseError",
        errorMessage: "[Error] Some error occurred while parsing URL parameter"
      }
    }
  }
}
