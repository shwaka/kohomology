import { createWorkerContext } from "@calculator/WorkerContext"

import { SampleWorkerFunc, SampleWorkerInput, SampleWorkerOutput, SampleWorkerState } from "./SampleWorker"

export const sampleWorkerContext = createWorkerContext<SampleWorkerInput, SampleWorkerOutput, SampleWorkerState, SampleWorkerFunc>()
