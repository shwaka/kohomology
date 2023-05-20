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

export class MyWorker {
  value: number
  onmessage: (e: MessageEvent<MyWorkerOutput>) => void

  constructor() {
    this.value = 0
    this.onmessage = (_) => { throw new Error("MyWorker is not initialized") }
  }

  postMessage(input: MyWorkerInput): void {
    this.value += input.value
    const outputUpdateState: MyWorkerOutput = {
      command: "updateState",
      key: "value",
      value: this.value,
    }
    this.onmessage({ data: outputUpdateState } as MessageEvent<MyWorkerOutput>)
    const output: MyWorkerOutput = {
      result: `value=${this.value}`
    }
    this.onmessage({ data: output } as MessageEvent<MyWorkerOutput>)
  }
}
