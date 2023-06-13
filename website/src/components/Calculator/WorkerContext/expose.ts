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

export type MessageOutput<WO, WS> =
  | MessageSendOutput<WO>
  | MessageOutputUpdateState<WS>

export type UpdateWorkerState<WS> = <K extends keyof WS>(...args: UpdateStateArgs<WS, K>) => void

export interface CallbackData<WI, WO, WS> {
  postWorkerOutput: (output: WO) => void
  updateState: UpdateWorkerState<WS>
}

export interface WorkerImpl<WI, WO> {
  onWorkerInput: (input: WI) => void
}

export interface ExposedWorkerImpl<WI, WO> {
  onmessage: (event: MessageEvent<WI>) => void
}

type UpdateStateArgs<WS, K = keyof WS> =
  K extends keyof WS
    ? [K, WS[K]]
    : never

export function expose<WI, WO, WS>(
  postMessage: (output: MessageOutput<WO, WS>) => void,
  getWorkerImpl: (callbackData: CallbackData<WI, WO, WS>) => WorkerImpl<WI, WO>
): ExposedWorkerImpl<WI, WO> {
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
  const onmessage = (event: MessageEvent<WI>): void => {
    workerImpl.onWorkerInput(event.data)
  }
  return {
    onmessage,
  }
}
