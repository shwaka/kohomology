import { createWorkerContext } from "@calculator/WorkerContext"

import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./worker/workerInterface"

const createWorker = (): Worker => new Worker(new URL("./worker/kohomology.worker.ts", import.meta.url))
export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(createWorker)
