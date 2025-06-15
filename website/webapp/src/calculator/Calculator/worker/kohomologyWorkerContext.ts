import { createWorkerContext } from "@calculator/WorkerContext"

import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

const createWorker = (): Worker => new Worker(new URL("./kohomology.worker.ts", import.meta.url))
export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(createWorker)
