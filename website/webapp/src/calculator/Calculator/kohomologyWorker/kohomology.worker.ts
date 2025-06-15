import { expose } from "@calculator/WorkerContext/expose"

import { KohomologyWorkerImpl } from "./KohomologyWorkerImpl"
import { KohomologyWorkerFunc, KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose<KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>(
  ctx.postMessage.bind(ctx),
  (callbackData) => new KohomologyWorkerImpl(callbackData),
)

onmessage = exposed.onmessage
