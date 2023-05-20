import { StyledMessage } from "../styled/message"
import { OutputFromState } from "../WorkerContext"

export const targetNames = ["self", "freeLoopSpace", "cyclic", "derivation"] as const
export type TargetName = (typeof targetNames)[number]

// inputs
export const inputCommands = ["updateJson", "computeCohomology", "dgaInfo", "computeCohomologyClass"] as const
export type InputCommand = (typeof inputCommands)[number]

export const showCohomologyCandidates = ["basis", "dim"] as const
export type ShowCohomology = (typeof showCohomologyCandidates)[number]

type UpdateJsonCommand = {
  command: "updateJson"
  json: string
}
type ComputeCohomologyComamnd = {
  command: "computeCohomology"
  targetName: TargetName
  minDegree: number
  maxDegree: number
  showCohomology: ShowCohomology
}
type ComputeCohomologyClassCommand = {
  command: "computeCohomologyClass"
  targetName: TargetName
  cocycleString: string
  showBasis: boolean
}
type NoArgCommand = {
  command: "dgaInfo"
}

export type WorkerInput = UpdateJsonCommand | ComputeCohomologyComamnd | ComputeCohomologyClassCommand | NoArgCommand

// outputs
export const outputCommands = ["printMessages", "showDgaInfo", "notifyInfo"] as const
export type OutputCommand = (typeof outputCommands)[number]

export type SendMessage = {
  command: "printMessages" | "showDgaInfo"
  messages: StyledMessage[]
}

export type WorkerStatus = "computing" | "idle"
export type WorkerInfo = {
  status: "idle"
} | {
  status: "computing"
  progress: number | null // should be between 0 and 1
}
export type NotifyInfo = {
  command: "notifyInfo"
  info: WorkerInfo
}

export type WorkerState = {
  json: string
}
export type UpdateState = OutputFromState<WorkerState>

export type WorkerOutput = SendMessage | NotifyInfo | UpdateState
