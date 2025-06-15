import { createWorkerContext } from "@calculator/WorkerContext"
import { MockWorker } from "@calculator/WorkerContext/__testutils__/MockWorker"
import { CallbackData } from "@calculator/WorkerContext/expose"

import { KohomologyWorkerImpl } from "../KohomologyWorkerImpl"
import { KohomologyWorkerFunc, KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState } from "../workerInterface"

function getWorkerImpl(
  callbackData: CallbackData<KohomologyWorkerOutput, KohomologyWorkerState>
): KohomologyWorkerImpl {
  return new KohomologyWorkerImpl({
    ...callbackData,
    log: (_message) => {
      // console.log(_message)
      return
    },
    error: (_message) => {
      // console.error(_message)
      return
    },
  })
}

function createWorker(): Worker {
  return new MockWorker(getWorkerImpl) as unknown as Worker
}

export const kohomologyWorkerContext = createWorkerContext<KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>(createWorker)
