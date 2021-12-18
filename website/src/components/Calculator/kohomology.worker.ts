import { FreeDGAWrapper } from "kohomology-js"
import { StyledMessage, toStyledMessage } from "./styled"
import { WorkerInput, WorkerOutput, TargetName } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

let dgaWrapper: FreeDGAWrapper | null = null

function updateJson(json: string): void {
  dgaWrapper = new FreeDGAWrapper(json)
}

function sendMessages(messages: StyledMessage | StyledMessage[]): void {
  if (messages instanceof Array) {
    const output: WorkerOutput = {
      command: "printMessages",
      messages: messages,
    }
    ctx.postMessage(output)
  } else {
    const output: WorkerOutput = {
      command: "printMessages",
      messages: [messages],
    }
    ctx.postMessage(output)
  }
}

function computeCohomology(targetName: TargetName, maxDegree: number): void {
  if (dgaWrapper === null) {
    throw new Error("Not initialized")
  }
  sendMessages(toStyledMessage(dgaWrapper.computationHeader(targetName)))
  for (let degree = 0; degree <= maxDegree; degree++) {
    sendMessages(toStyledMessage(dgaWrapper.computeCohomology(targetName, degree)))
  }
}

function computeCohomologyClass(targetName: TargetName, cocycleString: string): void {
  if (dgaWrapper === null) {
    throw new Error("Not initialized")
  }
  sendMessages(toStyledMessage(dgaWrapper.computeCohomologyClass(targetName, cocycleString)))
}

function showDgaInfo(): void {
  if (dgaWrapper === null) {
    throw new Error("Not initialized")
  }
  const output: WorkerOutput = {
    command: "showDgaInfo",
    messages: dgaWrapper.dgaInfo().map(toStyledMessage),
  }
  ctx.postMessage(output)
}

onmessage = function(e: MessageEvent<WorkerInput>) {
  console.log("Worker start")
  const input: WorkerInput = e.data
  console.log(input)
  switch (input.command) {
    case "updateJson":
      updateJson(input.json)
      break
    case "computeCohomology":
      computeCohomology(input.targetName, input.maxDegree)
      break
    case "dgaInfo":
      showDgaInfo()
      break
    case "computeCohomologyClass":
      computeCohomologyClass(input.targetName, input.cocycleString)
      break
  }
}
