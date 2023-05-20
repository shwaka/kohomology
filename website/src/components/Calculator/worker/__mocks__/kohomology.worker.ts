import { KohomologyMessageHandler } from "../KohomologyMessageHandler"
import { WorkerInput, WorkerOutput } from "../workerInterface"

// - KohomologyWorker.postMessage corresponds to KohomologyMessageHandler.onmessage
// - KohomologyWorker.onmessage corresponds to KohomologyMessageHandler.postMessage
export default class KohomologyWorker {
  onmessage: (e: MessageEvent<WorkerOutput>) => void
  messageHandler: KohomologyMessageHandler
  constructor() {
    this.onmessage = (_) => { throw new Error("WebWorker is not initialized") } // This will be set outside of this module.
    this.messageHandler = new KohomologyMessageHandler(
      (output) => this.onmessage({ data: output } as MessageEvent<WorkerOutput>),
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

  postMessage(input: WorkerInput): void {
    this.messageHandler.onmessage(input)
  }

  terminate(): void {
    this.onmessage = (_) => {
      throw new Error("WebWorker is already terminated")
    }
  }
}
