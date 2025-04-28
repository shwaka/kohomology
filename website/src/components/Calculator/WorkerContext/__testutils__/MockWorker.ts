import { CallbackData, expose, ExposedWorkerImpl, MessageInput, MessageOutput, WFBase, WorkerImpl } from "../expose"

export class MockWorker<WI, WO, WS, WF extends WFBase> {
  onmessage: (e: MessageEvent<MessageOutput<WO, WS, WF>>) => void
  private exposed: ExposedWorkerImpl<WI, WF>

  constructor(
    getWorkerImpl: (callbackData: CallbackData<WO, WS>) => WorkerImpl<WI, WF>,
  ) {
    // this.onmessage will be set from the user of MockWorker.
    this.onmessage = (_) => { throw new Error("MockWorker is not initialized") }
    this.exposed = expose<WI, WO, WS, WF>(
      this._onmessage.bind(this),
      getWorkerImpl,
    )
  }

  postMessage(input: MessageInput<WI, WF>): void {
    this.exposed.onmessage({ data: input } as MessageEvent<MessageInput<WI, WF>>)
  }

  private _onmessage(output: MessageOutput<WO, WS, WF>): void {
    window.setTimeout(() => {
      this.onmessage({ data: output } as MessageEvent<MessageOutput<WO, WS, WF>>)
    })
  }

  terminate(): void {
    this.onmessage = (_) => {
      throw new Error("MockWorker is already terminated")
    }
  }
}
