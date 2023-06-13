import { expose } from "../WorkerContext/expose"
import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

const exposed = expose<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(
  ctx.postMessage.bind(ctx),
  ({ postWorkerOutput, updateState }) => {
    const messageHandler = new KohomologyMessageHandler(postWorkerOutput, updateState)
    return {
      onWorkerInput: (input) => messageHandler.onmessage(input),
      workerFunc: {
        validateIdealGenerator: (generator: string) =>
          messageHandler.validateIdealGenerator(generator),
      },
    }
  }
)

onmessage = exposed.onmessage
