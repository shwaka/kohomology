import { MockWorker } from "@calculator/WorkerContext/__testutils__/MockWorker"
import { CallbackData, WorkerImpl } from "@calculator/WorkerContext/expose"

import { KohomologyMessageHandler } from "../KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "../workerInterface"

class KohomologyWorkerImpl implements WorkerImpl<WorkerInput, WorkerFunc> {
  messageHandler: KohomologyMessageHandler
  workerFunc: WorkerFunc

  constructor({ postWorkerOutput, updateState }: CallbackData<WorkerOutput, WorkerState>) {
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
    this.workerFunc = {
      validateIdealGenerator: (generator: string) =>
        this.messageHandler.validateIdealGenerator(generator),
      validateIdealGeneratorArray: (generatorArray: string[]) =>
        this.messageHandler.validateIdealGeneratorArray(generatorArray),
    }
  }

  onWorkerInput(input: WorkerInput): void {
    this.messageHandler.onmessage(input)
  }
}

export default class KohomologyWorker extends MockWorker<WorkerInput, WorkerOutput, WorkerState, WorkerFunc> {
  constructor() {
    super((callbackData) => {
      return new KohomologyWorkerImpl(callbackData)
    })
  }
}
