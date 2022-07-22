import { useLocation } from "@docusaurus/router"
import { useMemo } from "react"
import { compressJson, prettifyDGAJson } from "../utils"
import { dsvToJson, jsonToDSV } from "./dotSeparatedValues"

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
  format: "json" | "dsv"
}

export function createURLSearchParams(
  { dgaJson, format }: CreateURLSearchParamsArgs
): URLSearchParams | null {
  const urlSearchParams = new URLSearchParams()
  switch (format) {
    case "json":
      const compressedJson: string | null = compressJson(dgaJson)
      if (compressedJson === null) {
        return null
      }
      urlSearchParams.append("dgaJson", compressedJson)
      break
    case "dsv":
      const dsv = jsonToDSV(dgaJson)
      urlSearchParams.append("dgaDsv", dsv)
      break
  }
  return urlSearchParams
}

function useDgaJsonFromURLQuery(): string | null {
  const urlSearchParams = useQuery()
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

type QueryResult = QueryResultSuccess | QueryResultUnspecified | QueryResultParseError

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
