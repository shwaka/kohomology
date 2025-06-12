import { fromString } from "@calculator/styled/message"
import { CallbackData, UpdateWorkerState, WFBase, WorkerImpl } from "@calculator/WorkerContext/expose"

import { SendMessage } from "../"

export interface SampleWorkerInput {
  value: number
}

export type SampleWorkerOutput = SendMessage

export interface SampleWorkerState {
  value: number
}

export type SampleWorkerFunc = WFBase
// {
//   add: (value: number) => number
// }

export class SampleWorkerImpl implements WorkerImpl<SampleWorkerInput, SampleWorkerFunc> {
  value: number
  postWorkerOutput: (output: SampleWorkerOutput) => void
  updateState: UpdateWorkerState<SampleWorkerState>
  workerFunc: SampleWorkerFunc

  constructor({
    postWorkerOutput, updateState
  }: CallbackData<SampleWorkerOutput, SampleWorkerState>) {
    this.value = 0
    this.postWorkerOutput = postWorkerOutput
    this.updateState = updateState
    this.workerFunc = {
      // add: (value: number): number => {
      //   this.value += value
      //   this.updateState("value", this.value)
      //   return this.value
      // }
    }
  }

  onWorkerInput(input: SampleWorkerInput): void {
    this.value += input.value
    this.updateState("value", this.value)
    const message = fromString("success", `value=${this.value}`)
    const output: SampleWorkerOutput = {
      command: "printMessages",
      messages: [message],
    }
    this.postWorkerOutput(output)
  }
}
