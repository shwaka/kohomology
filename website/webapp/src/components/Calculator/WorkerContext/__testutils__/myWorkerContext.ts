import { createWorkerContext } from "../WorkerContext"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorker"

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>()
