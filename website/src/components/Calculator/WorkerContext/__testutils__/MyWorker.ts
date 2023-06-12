import { CallbackData } from "../expose"
import { MockWorker } from "./MockWorker"

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
