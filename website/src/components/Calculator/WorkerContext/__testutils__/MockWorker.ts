import { CallbackData, expose, ExposedWorkerImpl, WorkerImpl } from "../expose"

export class MockWorker<WI, WO> {
  onmessage: (e: MessageEvent<WO>) => void
  private exposed: ExposedWorkerImpl<WI, WO>

  constructor(
    getWorkerImpl: (callbackData: CallbackData<WI, WO>) => WorkerImpl<WI, WO>,
  ) {
    // this.onmessage will be set from the user of MockWorker.
    this.onmessage = (_) => { throw new Error("MockWorker is not initialized") }
    this.exposed = expose<WI, WO>(
      this._onmessage.bind(this),
      getWorkerImpl,
    )
  }

  postMessage(input: WI): void {
    this.exposed.onmessage({ data: input } as MessageEvent<WI>)
  }

  private _onmessage(output: WO): void {
    this.onmessage({ data: output } as MessageEvent<WO>)
  }
}
