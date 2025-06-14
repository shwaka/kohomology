import { createWorkerContext } from "../WorkerContext"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorker"
import { MyWorker } from "./MyWorker"

const createWorker: () => Worker = () => new MyWorker() as unknown as Worker
export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
