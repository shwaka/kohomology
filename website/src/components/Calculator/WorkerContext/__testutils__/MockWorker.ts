import { CallbackData, expose, ExposedWorkerImpl, MessageOutput, WorkerImpl } from "../expose"

export class MockWorker<WI, WO, WS> {
  onmessage: (e: MessageEvent<MessageOutput<WO, WS>>) => void
  private exposed: ExposedWorkerImpl<WI, WO>

  constructor(
    getWorkerImpl: (callbackData: CallbackData<WI, WO, WS>) => WorkerImpl<WI, WO>,
  ) {
    // this.onmessage will be set from the user of MockWorker.
    this.onmessage = (_) => { throw new Error("MockWorker is not initialized") }
    this.exposed = expose<WI, WO, WS>(
      this._onmessage.bind(this),
      getWorkerImpl,
    )
  }

  postMessage(input: WI): void {
    this.exposed.onmessage({ data: input } as MessageEvent<WI>)
  }

  private _onmessage(output: MessageOutput<WO, WS>): void {
    this.onmessage({ data: output } as MessageEvent<MessageOutput<WO, WS>>)
  }

  terminate(): void {
    this.onmessage = (_) => {
      throw new Error("MockWorker is already terminated")
    }
  }
}
