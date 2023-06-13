import { CallbackData, UpdateWorkerState, WorkerImpl } from "../expose"
import { MockWorker } from "./MockWorker"

export interface MyWorkerInput {
  value: number
}

export type MyWorkerOutput = {
  result: string
}

export type MyWorkerState = {
  value: number
}

export type MyWorkerFunc = {
  add: (value: number) => number
}

class MyWorkerImpl implements WorkerImpl<MyWorkerInput, MyWorkerOutput> {
  value: number
  postWorkerOutput: (output: MyWorkerOutput) => void
  updateState: UpdateWorkerState<MyWorkerState>

  constructor({ postWorkerOutput, updateState }: CallbackData<MyWorkerInput, MyWorkerOutput, MyWorkerState>) {
    this.value = 0
    this.postWorkerOutput = postWorkerOutput
    this.updateState = updateState
  }

  onWorkerInput(input: MyWorkerInput): void {
    this.value += input.value
    this.updateState("value", this.value)
    const output: MyWorkerOutput = {
      result: `value=${this.value}`
    }
    this.postWorkerOutput(output)
  }
}

export class MyWorker extends MockWorker<MyWorkerInput, MyWorkerOutput, MyWorkerState> {
  constructor() {
    super((callbackData) => {
      return new MyWorkerImpl(callbackData)
    })
  }
}
