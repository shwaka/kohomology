import { KohomologyMessageHandler } from "./KohomologyMessageHandler"
import { WorkerInput } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx: Worker = self as any
const messageHandler = new KohomologyMessageHandler(ctx.postMessage.bind(ctx))

onmessage = ((e: MessageEvent<WorkerInput>) => messageHandler.onmessage(e.data))
