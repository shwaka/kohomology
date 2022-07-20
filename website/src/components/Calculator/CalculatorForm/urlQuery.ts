import { useLocation } from "@docusaurus/router"
import { useMemo } from "react"
import { compressJson, prettifyDGAJson } from "../utils"

function useQuery(): URLSearchParams {
  // https://v5.reactrouter.com/web/example/query-parameters
  const { search } = useLocation()
  return useMemo(
    () => new URLSearchParams(search),
    [search]
  )
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

type QueryResult = QueryResultSuccess | QueryResultUnspecified | QueryResultParseError

export function useJsonFromURLQuery(): QueryResult {
  const urlSearchParams = useQuery()
  const dgaJson: string | null = urlSearchParams.get("dgaJson")
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
