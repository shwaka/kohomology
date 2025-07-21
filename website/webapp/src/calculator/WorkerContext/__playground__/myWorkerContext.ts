import { createWorkerContext } from ".."
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorkerInterface"

const createWorker = (): Worker => new Worker(new URL("./my.worker.ts", import.meta.url))
export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
