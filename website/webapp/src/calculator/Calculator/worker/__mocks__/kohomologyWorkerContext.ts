import { createWorkerContext } from "@calculator/WorkerContext"

import KohomologyWorker from "./kohomology.worker"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "../workerInterface"

function createWorker(): Worker {
  return new KohomologyWorker() as unknown as Worker
}

export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(createWorker)
