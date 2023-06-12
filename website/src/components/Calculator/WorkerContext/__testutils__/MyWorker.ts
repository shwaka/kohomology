import { CallbackData, expose, ExposedWorkerImpl, WorkerImpl } from "../expose"

export interface MyWorkerInput {
  value: number
}

export type MyWorkerOutput = {
  result: string
} | {
  command: "updateState"
  key: "value"
  value: number
}

class MyWorkerImpl {
  value: number
  postWorkerOutput: (output: MyWorkerOutput) => void

  constructor({ postWorkerOutput }: CallbackData<MyWorkerInput, MyWorkerOutput>) {
    this.value = 0
    this.postWorkerOutput = postWorkerOutput
  }

  onWorkerInput(input: MyWorkerInput): void {
    this.value += input.value
    const outputUpdateState: MyWorkerOutput = {
      command: "updateState",
      key: "value",
      value: this.value,
    }
    this.postWorkerOutput(outputUpdateState)
    const output: MyWorkerOutput = {
      result: `value=${this.value}`
    }
    this.postWorkerOutput(output)
  }
}

export class MockWorker<WI, WO> {
  onmessage: (e: MessageEvent<WO>) => void
  private exposed: ExposedWorkerImpl<WI, WO>

  constructor(
    getWorkerImpl: (callbackData: CallbackData<WI, WO>) => WorkerImpl<WI, WO>,
  ) {
    this.onmessage = (_) => { throw new Error("MyWorker is not initialized") }
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

export class MyWorker extends MockWorker<MyWorkerInput, MyWorkerOutput> {
  constructor() {
    super(({ postWorkerOutput }) => {
      const impl = new MyWorkerImpl({ postWorkerOutput })
      return {
        onWorkerInput: (input) => impl.onWorkerInput(input)
      }
    })
  }
}
