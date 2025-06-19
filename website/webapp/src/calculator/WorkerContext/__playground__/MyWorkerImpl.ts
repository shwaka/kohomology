import { CallbackData, UpdateWorkerState, WorkerImpl } from "../expose"
import { MyWorkerFunc, MyWorkerInput, MyWorkerOutput, MyWorkerState } from "./MyWorkerInterface"

class MyWorkerImpl implements WorkerImpl<MyWorkerInput, MyWorkerFunc> {
  value: number
  postWorkerOutput: (output: MyWorkerOutput) => void
  updateState: UpdateWorkerState<MyWorkerState>
  workerFunc: MyWorkerFunc

  constructor({ postWorkerOutput, updateState }: CallbackData<MyWorkerOutput, MyWorkerState>) {
    this.value = 0
    this.postWorkerOutput = postWorkerOutput
    this.updateState = updateState
    this.workerFunc = {
      add: (value: number): number => {
        this.value += value
        this.updateState("value", this.value)
        return this.value
      }
    }
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

export function getMyWorkerImpl(
  callbackData: CallbackData<MyWorkerOutput, MyWorkerState>
): MyWorkerImpl {
  return new MyWorkerImpl(callbackData)
}
