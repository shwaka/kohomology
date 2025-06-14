import { createWorkerContext } from "@calculator/WorkerContext"

import { SampleWorkerFunc, SampleWorkerInput, SampleWorkerOutput, SampleWorkerState } from "./SampleWorker"

const createWorker = (): Worker => new Worker(new URL("./sample.worker.ts", import.meta.url))
export const sampleWorkerContext = createWorkerContext<SampleWorkerInput, SampleWorkerOutput, SampleWorkerState, SampleWorkerFunc>(createWorker)
