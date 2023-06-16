import { compressJson } from "../jsonUtils"
import { TargetName } from "../worker/workerInterface"
import { ParamName } from "./ParamName"
import { dgaJsonToDsv } from "./dgaDsv"

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
      const dsv: string | null = dgaJsonToDsv(dgaJson)
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
  targetName: TargetName
}

export function createURLSearchParams(
  { dgaJson, format, targetName }: CreateURLSearchParamsArgs
): URLSearchParams {
  const urlSearchParams = new URLSearchParams()
  const paramForDga = getParamForDga(dgaJson, format)
  if (paramForDga !== null) {
    const [key, value] = paramForDga
    urlSearchParams.append(key, value)
  }
  urlSearchParams.append(ParamName.taretName, targetName)
  return urlSearchParams
}
