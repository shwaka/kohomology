export interface MyWorkerInput {
  value: number
}

export interface MyWorkerOutput {
  result: string
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
    const output: MyWorkerOutput = {
      result: `value=${this.value}`
    }
    this.onmessage({ data: output } as MessageEvent<MyWorkerOutput>)
  }
}
