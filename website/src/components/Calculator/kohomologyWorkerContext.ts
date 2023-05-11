import { createWorkerContext } from "./WorkerContext"
import { WorkerInput, WorkerOutput } from "./worker/workerInterface"

export const kohomologyWorkerContext = createWorkerContext<WorkerInput, WorkerOutput>()
