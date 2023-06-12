import { CallbackData, expose, ExposedWorkerImpl } from "../expose"

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

export class MyWorker {
  onmessage: (e: MessageEvent<MyWorkerOutput>) => void
  private exposed: ExposedWorkerImpl<MyWorkerInput, MyWorkerOutput>

  constructor() {
    this.onmessage = (_) => { throw new Error("MyWorker is not initialized") }
    this.exposed = expose<MyWorkerInput, MyWorkerOutput>(
      this._onmessage.bind(this),
      ({ postWorkerOutput }) => {
        const impl = new MyWorkerImpl({ postWorkerOutput })
        return {
          onWorkerInput: (input) => impl.onWorkerInput(input)
        }
      }
    )
  }

  postMessage(input: MyWorkerInput): void {
    this.exposed.onmessage({ data: input } as MessageEvent<MyWorkerInput>)
  }

  private _onmessage(output: MyWorkerOutput): void {
    this.onmessage({ data: output } as MessageEvent<MyWorkerOutput>)
  }
}
