import { createWorkerContext } from "@calculator/WorkerContext"

import { type SampleWorkerFunc, type SampleWorkerInput, type SampleWorkerOutput, type SampleWorkerState } from "./SampleWorker"

const createWorker = (): Worker => new Worker(new URL("./sample.worker.ts", import.meta.url))
export const sampleWorkerContext = createWorkerContext<SampleWorkerInput, SampleWorkerOutput, SampleWorkerState, SampleWorkerFunc>(createWorker)
