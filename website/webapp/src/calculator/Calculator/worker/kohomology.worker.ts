import { expose } from "@calculator/WorkerContext/expose"

import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(
  ctx.postMessage.bind(ctx),
  ({ postWorkerOutput, updateState }) => {
    const messageHandler = new KohomologyMessageHandler(postWorkerOutput, updateState)
    return {
      onWorkerInput: (input) => messageHandler.onmessage(input),
      workerFunc: {
        validateIdealGenerator: (generator: string) =>
          messageHandler.validateIdealGenerator(generator),
        validateIdealGeneratorArray: (generatorArray: string[]) =>
          messageHandler.validateIdealGeneratorArray(generatorArray),
      },
    }
  }
)

onmessage = exposed.onmessage
