import { expose } from "@calculator/WorkerContext/expose"

import { KohomologyWorkerImpl } from "./KohomologyWorkerImpl"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(
  ctx.postMessage.bind(ctx),
  (callbackData) => new KohomologyWorkerImpl(callbackData),
)

onmessage = exposed.onmessage
