import { expose } from "../WorkerContext/expose"
import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerInput, WorkerOutput } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any

const exposed = expose<WorkerInput, WorkerOutput>(
  ctx.postMessage.bind(ctx),
  ({ postWorkerOutput }) => {
    const messageHandler = new KohomologyMessageHandler(postWorkerOutput)
    return {
      onWorkerInput: (input) => messageHandler.onmessage(input)
    }
  }
)

onmessage = exposed.onmessage
