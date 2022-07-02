import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { StyledMessage } from "./styled"
import { WorkerInput, WorkerOutput } from "./workerInterface"

function formatMessage(styledMessage: StyledMessage): string {
  return styledMessage.strings.map((styledString) => styledString.content).join("")
}

test("computeCohomology", () => {
  const outputs: WorkerOutput[] = []
  const messageHandler = new KohomologyMessageHandler((output) => { outputs.push(output) })
  const updateJsonCommand: WorkerInput = {
    command: "updateJson",
    json: '[["x", 2, "zero"], ["y", 3, "x^2"]]',
  }
  messageHandler.onmessage({ data: updateJsonCommand } as MessageEvent<WorkerInput>)
  const computeCohomologyCommand: WorkerInput = {
    command: "computeCohomology",
    targetName: "self",
    minDegree: 0,
    maxDegree: 4,
    showCohomology: "basis",
  }
  messageHandler.onmessage({ data: computeCohomologyCommand } as MessageEvent<WorkerInput>)
  expect(outputs.length).toBe(6)
  expect(outputs[0].messages[0].strings[0].content).toEqual("Cohomology of ")
  expect(formatMessage(outputs[0].messages[0])).toEqual("Cohomology of (Λ(x, y), d) is")
  expect(formatMessage(outputs[1].messages[0])).toEqual("H^{0} =\\ \\mathbb{Q}\\{[1]\\}")
  expect(formatMessage(outputs[2].messages[0])).toEqual("H^{1} =\\ 0")
  expect(formatMessage(outputs[3].messages[0])).toEqual("H^{2} =\\ \\mathbb{Q}\\{[x]\\}")
  expect(formatMessage(outputs[4].messages[0])).toEqual("H^{3} =\\ 0")
  expect(formatMessage(outputs[5].messages[0])).toEqual("H^{4} =\\ 0")
})
