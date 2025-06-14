import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import "./forceEsm" // force ESM in *.worker.ts

export type MessageSendInput<WI> = {
  type: "input"
  value: WI
}

// The type of args must be any (not unknown, unknown[]).
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type WFBase = Record<string | number | symbol, (...args: any) => unknown>

export type MessageInputCallFunc<WF extends WFBase> =
  {
    [K in keyof WF]: {
      type: "callFunc"
      key: K
      args: Parameters<WF[K]>
      id: string
    }
  }[keyof WF]

export type MessageInput<WI, WF extends WFBase> =
  | MessageSendInput<WI>
  | MessageInputCallFunc<WF>

export type MessageSendOutput<WO> = {
  type: "output"
  value: WO
}

export type MessageOutputUpdateState<WS> =
  {
    [K in keyof WS]: {
      type: "updateState"
      key: K
      value: WS[K]
    }
  }[keyof WS]

export type MessageOutputFuncResult<WF extends WFBase> =
  {
    [K in keyof WF]: {
      type: "funcResult"
      key: K
      result: ReturnType<WF[K]>
      id: string
    }
  }[keyof WF]

export type MessageOutput<WO, WS, WF extends WFBase> =
  | MessageSendOutput<WO>
  | MessageOutputUpdateState<WS>
  | MessageOutputFuncResult<WF>

export type UpdateWorkerState<WS> = <K extends keyof WS>(...args: UpdateStateArgs<WS, K>) => void

export interface CallbackData<WO, WS> {
  postWorkerOutput: (output: WO) => void
  updateState: UpdateWorkerState<WS>
}

export interface WorkerImpl<WI, WF> {
  onWorkerInput: (input: WI) => void
  workerFunc: WF
}

export interface ExposedWorkerImpl<WI, WF extends WFBase> {
  onmessage: (event: MessageEvent<MessageInput<WI, WF>>) => void
}

type UpdateStateArgs<WS, K = keyof WS> =
  K extends keyof WS
    ? [K, WS[K]]
    : never

export function expose<WI, WO, WS, WF extends WFBase>(
  postMessage: (output: MessageOutput<WO, WS, WF>) => void,
  getWorkerImpl: (callbackData: CallbackData<WO, WS>) => WorkerImpl<WI, WF>
): ExposedWorkerImpl<WI, WF> {
  const postWorkerOutput = (output: WO): void => {
    postMessage({
      type: "output",
      value: output,
    })
  }
  const updateState = <K extends keyof WS>(...args: UpdateStateArgs<WS, K>): void => {
    // const [key, value] = args
    const key = args[0]
    const value = args[1]
    const output: MessageOutputUpdateState<WS> = {
      type: "updateState",
      key,
      value,
    }
    postMessage(output)
  }
  const workerImpl = getWorkerImpl({ postWorkerOutput, updateState })
  const call = (input: MessageInputCallFunc<WF>): void => {
    const result = workerImpl.workerFunc[input.key](...input.args)
    const output: MessageOutputFuncResult<WF> = {
      type: "funcResult",
      key: input.key,
      result: result as MessageOutputFuncResult<WF>["result"],
      id: input.id,
    }
    postMessage(output)
  }
  const onmessage = (event: MessageEvent<MessageInput<WI, WF>>): void => {
    const input: MessageInput<WI, WF> = event.data
    switch (input.type){
      case "input":
        workerImpl.onWorkerInput(input.value)
        return
      case "callFunc":
        call(input)
        return
      default:
        throw new ExhaustivityError(input, "onmessage is not exhaustive!")
    }
  }
  return {
    onmessage,
  }
}
