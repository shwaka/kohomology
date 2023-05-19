import { createWorkerContext } from "../WorkerContext"
import { MyWorkerInput, MyWorkerOutput } from "./MyWorker"

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput>()
