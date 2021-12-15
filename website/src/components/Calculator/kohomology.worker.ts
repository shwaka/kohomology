import { WorkerInput, WorkerOutput, TargetName } from "./workerInterface"
import { FreeDGAWrapper } from "kohomology-js"
import { StyledMessageKt } from "kohomology-js/build/compileSync/kotlin/kohomology-js"
import { StyledMessage, toStyledMessage } from "./styled"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

let dgaWrapper: FreeDGAWrapper | null = null

function updateJson(json: string): void {
  dgaWrapper = new FreeDGAWrapper(json)
}

function computeCohomology(targetName: TargetName, maxDegree: number): void {
  if (dgaWrapper === null) {
    throw new Error("Not initialized")
  }
  const messages: StyledMessage[] = []
  messages.push(toStyledMessage(dgaWrapper.computationHeader(targetName)))
  for (let degree = 0; degree <= maxDegree; degree++) {
    messages.push(toStyledMessage(dgaWrapper.computeCohomology(targetName, degree)))
  }
  const output: WorkerOutput = {
    messages: messages
  }
  console.log(messages)
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
  }
}
