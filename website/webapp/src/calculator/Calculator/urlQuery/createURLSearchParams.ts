import { compressJson } from "../jsonUtils"
import { dgaJsonToDsv } from "./dgaDsv"
import { idealJsonToDsv } from "./idealDsv"
import { ParamName } from "./ParamName"
import { TargetName } from "../worker/workerInterface"

type DgaEncodingFormat = "json" | "dsv" | "auto"

function getParamForDga(dgaJson: string, format: DgaEncodingFormat): [ParamName, string] | null {
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
  format: DgaEncodingFormat
  idealJson: string
  targetName: TargetName
}

export function createURLSearchParams(
  { dgaJson, format, idealJson, targetName }: CreateURLSearchParamsArgs
): URLSearchParams {
  const urlSearchParams = new URLSearchParams()
  const paramForDga = getParamForDga(dgaJson, format)
  if (paramForDga !== null) {
    const [key, value] = paramForDga
    urlSearchParams.append(key, value)
  }
  const idealDsv = idealJsonToDsv(idealJson)
  if ((idealDsv !== null) && (idealDsv !== "")) {
    urlSearchParams.append(ParamName.idealDsv, idealDsv)
  }
  urlSearchParams.append(ParamName.taretName, targetName)
  return urlSearchParams
}
