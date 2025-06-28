import { createWorkerContext } from "@calculator/WorkerContext"
import { MockWorker } from "@calculator/WorkerContext/__testutils__/MockWorker"
import { GetWorkerImpl } from "@calculator/WorkerContext/expose"

import { getKohomologyWorkerImpl } from "../KohomologyWorkerImpl"
import { KohomologyWorkerFunc, KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState } from "../workerInterface"

// Used through moduleNameMapper in jest.config.js

const getWorkerImpl: GetWorkerImpl<KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc> =
  (callbackData) => getKohomologyWorkerImpl({
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

function createWorker(): Worker {
  return new MockWorker(getWorkerImpl) as unknown as Worker
}

export const kohomologyWorkerContext = createWorkerContext<KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>(createWorker)
