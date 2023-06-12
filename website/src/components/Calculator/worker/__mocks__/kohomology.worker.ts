import { MockWorker } from "../../WorkerContext/__testutils__/MockWorker"
import { CallbackData, WorkerImpl } from "../../WorkerContext/expose"
import { KohomologyMessageHandler } from "../KohomologyMessageHandler"
import { WorkerInput, WorkerOutput, WorkerState } from "../workerInterface"

class KohomologyWorkerImpl implements WorkerImpl<WorkerInput, WorkerOutput> {
  messageHandler: KohomologyMessageHandler

  constructor({ postWorkerOutput, updateState }: CallbackData<WorkerInput, WorkerOutput, WorkerState>) {
    this.messageHandler = new KohomologyMessageHandler(
      postWorkerOutput,
      updateState,
      (_message) => {
        // console.log(_message)
        return
      },
      (_message) => {
        // console.error(_message)
        return
      },
    )
  }

  onWorkerInput(input: WorkerInput): void {
    this.messageHandler.onmessage(input)
  }
}

export default class KohomologyWorker extends MockWorker<WorkerInput, WorkerOutput, WorkerState> {
  constructor() {
    super((callbackData) => {
      return new KohomologyWorkerImpl(callbackData)
    })
  }
}
