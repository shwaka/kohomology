import { MessageOutput, MessageOutputUpdateState, MessageSendOutput } from "@calculator/WorkerContext/expose"

import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

function expectSendMessage(output: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>): asserts output is MessageSendOutput<WorkerOutput> {
  expect(output.type).toBe("output")
  // expect(output.command).toBeOneOf(["printMessages", "showDgaInfo"])
}

function expectUpdateState(output: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>): asserts output is MessageOutputUpdateState<WorkerState> {
  expect(output.type).toBe("updateState")
  // expect(output.command).toBeOneOf(["updateState"])
}

function expectUpdateStateOfKey(output: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>, key: keyof WorkerState): asserts output is MessageOutputUpdateState<WorkerState> {
  expectUpdateState(output)
  expect(output.key).toBe(key)
}

test("computeCohomology", () => {
  const outputs: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>[] = []
  const messageHandler = new KohomologyMessageHandler({
    postWorkerOutput: (output) => { outputs.push({ type: "output", value: output }) },
    updateState: (key, value) => {
      // See comments in updateState in expose.ts for this cast.
      const output = { type: "updateState", key, value } as MessageOutputUpdateState<WorkerState>
      outputs.push(output)
    },
    log: (_) => { return },
    error: (_) => { return },
  })

  // updateJson
  const updateJsonCommand: WorkerInput = {
    command: "updateJson",
    json: '[["x", 2, "zero"], ["y", 3, "x^2"]]',
  }
  messageHandler.onmessage(updateJsonCommand)
  const expectedLengthUpdateJson = 6
  expect(outputs.length).toBe(expectedLengthUpdateJson)
  expectUpdateStateOfKey(outputs[0], "workerInfo")
  expectUpdateStateOfKey(outputs[1], "json")
  expectUpdateStateOfKey(outputs[2], "idealJson")
  expectUpdateStateOfKey(outputs[3], "dgaInfo")
  expectUpdateStateOfKey(outputs[4], "idealInfo")
  expectUpdateStateOfKey(outputs[5], "workerInfo")

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

  // check first message
  expect(messageOutput0.value.messages.length).toBe(1)
  expect(messageOutput0.value.messages[0].strings[0].content).toEqual("Computing ")
  // check second message
  expect(messageOutput1.value.messages.length).toBe(maxDegree + 1)
  expect(messageOutput0.value.messages[0].plainString).toEqual(`Computing $H^n(Λ(x, y), d)$ for $0 \\leq n \\leq ${maxDegree}$`)
  expect(messageOutput1.value.messages[0].plainString).toEqual("$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$")
  expect(messageOutput1.value.messages[1].plainString).toEqual("$H^{1} =\\  0$")
  expect(messageOutput1.value.messages[2].plainString).toEqual("$H^{2} =\\  \\mathbb{Q}\\{ [x] \\}$")
  for (let degree = 3; degree <= maxDegree; degree++) {
    expect(messageOutput1.value.messages[degree].plainString).toEqual(`$H^{${degree}} =\\  0$`)
  }
})
