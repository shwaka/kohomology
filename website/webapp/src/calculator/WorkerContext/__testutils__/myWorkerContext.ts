import { getMyWorkerImpl } from "../__playground__/MyWorkerImpl"
import { createWorkerContext } from "../WorkerContext"
import { MockWorker } from "./MockWorker"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "../__playground__/MyWorkerInterface"

const createWorker = (): Worker => new MockWorker(getMyWorkerImpl) as unknown as Worker

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
