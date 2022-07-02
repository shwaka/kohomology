import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerInput, WorkerOutput } from "./workerInterface"

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
    maxDegree: 5,
    showCohomology: "basis",
  }
  messageHandler.onmessage({ data: computeCohomologyCommand } as MessageEvent<WorkerInput>)
  expect(outputs[0].messages[0].strings[0].content).toEqual("Cohomology of ")
})
