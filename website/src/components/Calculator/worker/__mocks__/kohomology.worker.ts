import { MockWorker } from "../../WorkerContext/__testutils__/MockWorker"
import { CallbackData } from "../../WorkerContext/expose"
import { KohomologyMessageHandler } from "../KohomologyMessageHandler"
import { WorkerInput, WorkerOutput } from "../workerInterface"

class KohomologyWorkerImpl {
  messageHandler: KohomologyMessageHandler

  constructor({ postWorkerOutput }: CallbackData<WorkerInput, WorkerOutput>) {
    this.messageHandler = new KohomologyMessageHandler(
      (output) => postWorkerOutput(output),
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

export default class KohomologyWorker extends MockWorker<WorkerInput, WorkerOutput> {
  constructor() {
    super(({ postWorkerOutput }) => {
      const impl = new KohomologyWorkerImpl({ postWorkerOutput })
      return {
        onWorkerInput: (input) => impl.onWorkerInput(input),
      }
    })
  }

  terminate(): void {
    this.onmessage = (_) => {
      throw new Error("WebWorker is already terminated")
    }
  }
}
