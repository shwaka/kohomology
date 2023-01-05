import { formatStyledMessage } from "../styled/message"
import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { SendMessage, WorkerInput, WorkerOutput } from "./workerInterface"

function expectSendMessage(output: WorkerOutput): asserts output is SendMessage {
  expect(output.command).toBeOneOf(["printMessages", "showDgaInfo"])
}

test("computeCohomology", () => {
  const outputs: WorkerOutput[] = []
  const messageHandler = new KohomologyMessageHandler(
    (output) => { outputs.push(output) },
    (_) => { return },
    (_) => { return },
  )
  const updateJsonCommand: WorkerInput = {
    command: "updateJson",
    json: '[["x", 2, "zero"], ["y", 3, "x^2"]]',
  }
  messageHandler.onmessage(updateJsonCommand)
  const computeCohomologyCommand: WorkerInput = {
    command: "computeCohomology",
    targetName: "self",
    minDegree: 0,
    maxDegree: 4,
    showCohomology: "basis",
  }
  messageHandler.onmessage(computeCohomologyCommand)
  expect(outputs.length).toBe(4)
  // check output types
  const messageOutput0 = outputs[0]
  expectSendMessage(messageOutput0)
  const messageOutput1 = outputs[2]
  expectSendMessage(messageOutput1)
  // check first message
  expect(messageOutput0.messages.length).toBe(1)
  expect(messageOutput0.messages[0].strings[0].content).toEqual("Computing ")
  // check second message
  expect(messageOutput1.messages.length).toBe(5)
  expect(formatStyledMessage(messageOutput0.messages[0])).toEqual("Computing H^n(Λ(x, y), d) for 0 \\leq n \\leq 4")
  expect(formatStyledMessage(messageOutput1.messages[0])).toEqual("H^{0} =\\ \\mathbb{Q}\\{[1]\\}")
  expect(formatStyledMessage(messageOutput1.messages[1])).toEqual("H^{1} =\\ 0")
  expect(formatStyledMessage(messageOutput1.messages[2])).toEqual("H^{2} =\\ \\mathbb{Q}\\{[x]\\}")
  expect(formatStyledMessage(messageOutput1.messages[3])).toEqual("H^{3} =\\ 0")
  expect(formatStyledMessage(messageOutput1.messages[4])).toEqual("H^{4} =\\ 0")
})
