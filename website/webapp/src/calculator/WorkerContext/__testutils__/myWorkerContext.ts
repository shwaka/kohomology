import { CallbackData } from "../expose"
import { createWorkerContext } from "../WorkerContext"
import { MockWorker } from "./MockWorker"
import { MyWorkerImpl } from "./MyWorkerImpl"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorkerInterface"

function getWorkerImpl(
  callbackData: CallbackData<MyWorkerOutput, MyWorkerState>
): MyWorkerImpl {
  return new MyWorkerImpl(callbackData)
}

const createWorker = (): Worker => new MockWorker(getWorkerImpl) as unknown as Worker

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
