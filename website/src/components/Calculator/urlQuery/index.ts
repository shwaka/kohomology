import { compressJson, prettifyDGAJson } from "../jsonUtils"
import { dsvToJson, jsonToDSV } from "./dotSeparatedValues"
import { useURLSearchParams } from "./useURLSearchParams"

type EncodingFormat = "json" | "dsv" | "auto"

function getParam(dgaJson: string, format: EncodingFormat): ["dgaJson" | "dgaDsv", string] | null {
  switch (format) {
    case "json": {
      const compressedJson: string | null = compressJson(dgaJson)
      if (compressedJson === null) {
        return null
      }
      return ["dgaJson", compressedJson]
    }
    case "dsv": {
      const dsv: string | null = jsonToDSV(dgaJson)
      if (dsv === null) {
        return null
      }
      return ["dgaDsv", dsv]
    }
    case "auto": {
      const paramsForDsv = getParam(dgaJson, "dsv")
      if (paramsForDsv !== null) {
        return paramsForDsv
      }
      const paramsForJson = getParam(dgaJson, "json")
      if (paramsForJson !== null) {
        return paramsForJson
      }
      return null
    }
  }
}

interface CreateURLSearchParamsArgs {
  dgaJson: string
  format: EncodingFormat
}

export function createURLSearchParams(
  { dgaJson, format }: CreateURLSearchParamsArgs
): URLSearchParams | null {
  const urlSearchParams = new URLSearchParams()
  const param = getParam(dgaJson, format)
  if (param === null) {
    return null
  }
  const [key, value] = param
  urlSearchParams.append(key, value)
  return urlSearchParams
}

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
