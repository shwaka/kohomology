import { CallbackData } from "../expose"
import { createWorkerContext } from "../WorkerContext"
import { MockWorker } from "./MockWorker"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorker"
import { MyWorkerImpl } from "./MyWorker"

function getWorkerImpl(
  callbackData: CallbackData<MyWorkerOutput, MyWorkerState>
): MyWorkerImpl {
  return new MyWorkerImpl(callbackData)
}

const createWorker = (): Worker => new MockWorker(getWorkerImpl) as unknown as Worker

export const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput, MyWorkerState, MyWorkerFunc>(createWorker)
