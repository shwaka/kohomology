import { expose } from "@calculator/WorkerContext/expose"

import { getSampleWorkerImpl } from "./SampleWorker"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose(ctx.postMessage.bind(ctx), getSampleWorkerImpl)

onmessage = exposed.onmessage
