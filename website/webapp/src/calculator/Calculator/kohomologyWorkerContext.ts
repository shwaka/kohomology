import { createWorkerContext } from "./WorkerContext"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./worker/workerInterface"

export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>()
