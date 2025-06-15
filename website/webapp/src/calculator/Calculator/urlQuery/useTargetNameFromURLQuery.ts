import { ParamName } from "./ParamName"
import { URLQueryResult } from "./URLQueryResult"
import { useURLSearchParams } from "./useURLSearchParams"
import { TargetName, targetNames } from "../kohomologyWorker/workerInterface"

export function useTargetNameFromURLQuery(): URLQueryResult<TargetName> {
  const urlSearchParams = useURLSearchParams()
  const targetName: string | null = urlSearchParams.get(ParamName.taretName)
  if (targetName === null) {
    return {
      type: "unspecified"
    }
  }
  if ((targetNames as readonly string[]).includes(targetName)) {
    return {
      type: "success",
      value: targetName as TargetName,
    }
  }
  return {
    type: "error",
    message: `Invalid target name: ${targetName}`
  }
}
