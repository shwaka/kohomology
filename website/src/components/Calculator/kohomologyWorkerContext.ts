import { createWorkerContext } from "./WorkerContext"
import { WorkerInput, WorkerOutput, WorkerState } from "./worker/workerInterface"

export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput, WorkerState>()
