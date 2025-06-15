import { MockWorker } from "@calculator/WorkerContext/__testutils__/MockWorker"

import { KohomologyWorkerImpl } from "../KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "../workerInterface"

export default class KohomologyWorker extends MockWorker<WorkerInput, WorkerOutput, WorkerState, WorkerFunc> {
  constructor() {
    super((callbackData) => {
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
    })
  }
}
