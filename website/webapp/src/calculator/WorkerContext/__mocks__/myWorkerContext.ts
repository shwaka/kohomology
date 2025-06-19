import { getMyWorkerImpl } from "../__playground__/MyWorkerImpl"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "../__playground__/MyWorkerInterface"
import { MockWorker } from "../__testutils__/MockWorker"
import { createWorkerContext } from "../WorkerContext"

// Used through moduleNameMapper in jest.config.js

const createWorker = (): Worker => new MockWorker(getMyWorkerImpl) as unknown as Worker

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
