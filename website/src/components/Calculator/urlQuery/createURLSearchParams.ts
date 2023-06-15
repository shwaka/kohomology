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
