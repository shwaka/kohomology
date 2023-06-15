import { compressJson, prettifyDGAJson } from "../jsonUtils"
import { dsvToJson, jsonToDSV } from "./dotSeparatedValues"
import { ParamName } from "./ParamName"
import { useURLSearchParams } from "./useURLSearchParams"

type EncodingFormat = "json" | "dsv" | "auto"

function getParamForDga(dgaJson: string, format: EncodingFormat): [ParamName, string] | null {
  switch (format) {
    case "json": {
      const compressedJson: string | null = compressJson(dgaJson)
      if (compressedJson === null) {
        return null
      }
      return [ParamName.dgaJson, compressedJson]
    }
    case "dsv": {
      const dsv: string | null = jsonToDSV(dgaJson)
      if (dsv === null) {
        return null
      }
      return [ParamName.dgaDsv, dsv]
    }
    case "auto": {
      const paramsForDsv = getParamForDga(dgaJson, "dsv")
      if (paramsForDsv !== null) {
        return paramsForDsv
      }
      const paramsForJson = getParamForDga(dgaJson, "json")
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
  const paramForDga = getParamForDga(dgaJson, format)
  if (paramForDga === null) {
    return null
  }
  const [key, value] = paramForDga
  urlSearchParams.append(key, value)
  return urlSearchParams
}
