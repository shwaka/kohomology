import { StyledMessageKt } from "kohomology-js/build/compileSync/kotlin/kohomology-js"
import { StyledMessage } from "./styled"

export const targetNames = ["self", "freeLoopSpace"] as const
export type TargetName = (typeof targetNames)[number]

export const commands = ["updateJson", "computeCohomology", "dgaInfo"] as const
export type Command = (typeof commands)[number]

type UpdateJsonCommand = {
  command: "updateJson",
  json: string,
}
type ComputeCohomologyComamnd = {
  command: "computeCohomology",
  targetName: TargetName,
  maxDegree: number,
}
type NoArgCommand = {
  command: "dgaInfo"
}

export type WorkerInput = UpdateJsonCommand | ComputeCohomologyComamnd | NoArgCommand

export interface WorkerOutput {
  messages: StyledMessage[]
}
