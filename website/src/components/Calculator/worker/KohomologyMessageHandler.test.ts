import { formatStyledMessage } from "../styled/message"
import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { NotifyInfo, SendMessage, UpdateState, WorkerInput, WorkerOutput, WorkerStatus } from "./workerInterface"

function expectSendMessage(output: WorkerOutput): asserts output is SendMessage {
  expect(output.command).toBeOneOf(["printMessages", "showDgaInfo"])
}

function expectUpdateState(output: WorkerOutput): asserts output is UpdateState {
  expect(output.command).toBeOneOf(["updateState"])
}

function expectNotifyInfo(output: WorkerOutput): asserts output is NotifyInfo {
  expect(output.command).toBeOneOf(["notifyInfo"])
}

function expectNotifyInfoOfStatus(
  output: WorkerOutput,
  status: WorkerStatus,
): asserts output is NotifyInfo {
  expectNotifyInfo(output)
  expect(output.info.status).toBe(status)
}

test("computeCohomology", () => {
  const outputs: WorkerOutput[] = []
  const messageHandler = new KohomologyMessageHandler(
    (output) => { outputs.push(output) },
    (_) => { return },
    (_) => { return },
  )

  // updateJson
  const updateJsonCommand: WorkerInput = {
    command: "updateJson",
    json: '[["x", 2, "zero"], ["y", 3, "x^2"]]',
  }
  messageHandler.onmessage(updateJsonCommand)
  const expectedLengthUpdateJson = 3
  expect(outputs.length).toBe(expectedLengthUpdateJson)
  expectNotifyInfoOfStatus(outputs[0], "computing")
  expectUpdateState(outputs[1])
  expectNotifyInfoOfStatus(outputs[2], "idle")

  // computeCohomology
  const maxDegree = 4 // must be >= 3
  const computeCohomologyCommand: WorkerInput = {
    command: "computeCohomology",
    targetName: "self",
    minDegree: 0,
    maxDegree: maxDegree,
    showCohomology: "basis",
  }
  messageHandler.onmessage(computeCohomologyCommand)
  const expectedLengthComputeCohomology = 5
  expect(outputs.length).toBe(expectedLengthUpdateJson + expectedLengthComputeCohomology)

  // check output types
  const messageOutput0 = outputs[expectedLengthUpdateJson + 1]
  expectSendMessage(messageOutput0)
  const messageOutput1 = outputs[expectedLengthUpdateJson + 3]
  expectSendMessage(messageOutput1)
  expectNotifyInfoOfStatus(outputs[expectedLengthUpdateJson], "computing")
  expectNotifyInfoOfStatus(outputs[expectedLengthUpdateJson + 2], "computing")
  expectNotifyInfoOfStatus(outputs[expectedLengthUpdateJson + 4], "idle")

  // check first message
  expect(messageOutput0.messages.length).toBe(1)
  expect(messageOutput0.messages[0].strings[0].content).toEqual("Computing ")
  // check second message
  expect(messageOutput1.messages.length).toBe(maxDegree + 1)
  expect(formatStyledMessage(messageOutput0.messages[0])).toEqual(`Computing H^n(Λ(x, y), d) for 0 \\leq n \\leq ${maxDegree}`)
  expect(formatStyledMessage(messageOutput1.messages[0])).toEqual("H^{0} =\\ \\mathbb{Q}\\{[1]\\}")
  expect(formatStyledMessage(messageOutput1.messages[1])).toEqual("H^{1} =\\ 0")
  expect(formatStyledMessage(messageOutput1.messages[2])).toEqual("H^{2} =\\ \\mathbb{Q}\\{[x]\\}")
  for (let degree = 3; degree <= maxDegree; degree++) {
    expect(formatStyledMessage(messageOutput1.messages[degree])).toEqual(`H^{${degree}} =\\ 0`)
  }
})
