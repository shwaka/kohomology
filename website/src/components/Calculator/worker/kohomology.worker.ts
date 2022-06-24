import { FreeDGAWrapper } from "kohomology-js"
import { fromString, StyledMessage, toStyledMessage } from "./styled"
import { WorkerInput, WorkerOutput, TargetName } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

let dgaWrapper: FreeDGAWrapper | null = null

function assertInitialized(dgaWrapper: FreeDGAWrapper | null): asserts dgaWrapper is FreeDGAWrapper {
  if (dgaWrapper === null) {
    throw new Error("Not initialized")
  }
}

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

function computeCohomology(targetName: TargetName, minDegree: number, maxDegree: number): void {
  assertInitialized(dgaWrapper)
  sendMessages(toStyledMessage(dgaWrapper.computationHeader(targetName)))
  for (let degree = minDegree; degree <= maxDegree; degree++) {
    sendMessages(toStyledMessage(dgaWrapper.computeCohomology(targetName, degree)))
  }
}

function computeCohomologyClass(targetName: TargetName, cocycleString: string): void {
  assertInitialized(dgaWrapper)
  sendMessages(toStyledMessage(dgaWrapper.computeCohomologyClass(targetName, cocycleString)))
}

function showDgaInfo(): void {
  assertInitialized(dgaWrapper)
  const output: WorkerOutput = {
    command: "showDgaInfo",
    messages: dgaWrapper.dgaInfo().map(toStyledMessage),
  }
  ctx.postMessage(output)
}

function assertUnreachable(_: never, message: string): never {
  throw new Error(`This can't happen! (${message})`)
}

onmessage = function(e: MessageEvent<WorkerInput>) {
  console.log("Worker start")
  const input: WorkerInput = e.data
  console.log(input)
  try {
    switch (input.command) {
      case "updateJson":
        updateJson(input.json)
        break
      case "computeCohomology":
        computeCohomology(input.targetName, input.minDegree, input.maxDegree)
        break
      case "dgaInfo":
        showDgaInfo()
        break
      case "computeCohomologyClass":
        computeCohomologyClass(input.targetName, input.cocycleString)
        break
      default:
        assertUnreachable(input, "Invalid command")
    }
  } catch (error: unknown) {
    if (error instanceof Error) {
      sendMessages(fromString("error", error.message))
    }
    console.error(error)
  }
}
