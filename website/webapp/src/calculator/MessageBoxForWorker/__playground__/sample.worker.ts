import { expose } from "@calculator/WorkerContext/expose"

import { getSampleWorkerImpl } from "./SampleWorker"

const ctx = self as unknown as Worker

const exposed = expose(ctx.postMessage.bind(ctx), getSampleWorkerImpl)

onmessage = exposed.onmessage
