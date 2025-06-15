import { expose } from "@calculator/WorkerContext/expose"

import { SampleWorkerFunc, SampleWorkerImpl, SampleWorkerInput, SampleWorkerOutput, SampleWorkerState } from "./SampleWorker"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose<SampleWorkerInput, SampleWorkerOutput, SampleWorkerState, SampleWorkerFunc>(
  ctx.postMessage.bind(ctx),
  (callbackData) => new SampleWorkerImpl(callbackData),
)

onmessage = exposed.onmessage
