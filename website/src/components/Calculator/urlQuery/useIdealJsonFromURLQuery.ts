import { ParamName } from "./ParamName"
import { URLQueryResult } from "./URLQueryResult"
import { idealDsvToJson } from "./idealDsv"
import { useURLSearchParams } from "./useURLSearchParams"

function getIdealJsonFromURLQuery(urlSearchParams: URLSearchParams): string | null {
  const idealDsv: string | null = urlSearchParams.get(ParamName.idealDsv)
  if (idealDsv !== null) {
    return idealDsvToJson(idealDsv)
  }
  return null
}

export function useIdealJsonFromURLQuery(): URLQueryResult<string> {
  const urlSearchParams = useURLSearchParams()
  try {
    const idealJson: string | null = getIdealJsonFromURLQuery(urlSearchParams)
    if (idealJson === null){
      return {
        type: "unspecified",
      }
    }
    return {
      type: "success",
      value: idealJson,
    }
  } catch (e) {
    if (e instanceof Error) {
      return {
        type: "error",
        message: e.message,
      }
    } else {
      console.error(e)
      return {
        type: "error",
        message: "[Error] Some error occurred while parsing URL parameter",
      }
    }
  }
}
