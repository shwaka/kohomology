import { expose } from "../WorkerContext/expose"
import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

const exposed = expose<WorkerInput, WorkerOutput, WorkerState>(
  ctx.postMessage.bind(ctx),
  ({ postWorkerOutput, updateState }) => {
    const messageHandler = new KohomologyMessageHandler(postWorkerOutput, updateState)
    return {
      onWorkerInput: (input) => messageHandler.onmessage(input)
    }
  }
)

onmessage = exposed.onmessage
