import { createWorkerContext } from "../WorkerContext"
import { MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorker"

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState>()
