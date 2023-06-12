export type MessageOutputUpdateState<S, K = keyof S> = K extends keyof S ? {
  type: "updateState"
  key: K
  value: S[K]
} : never

export type MessageOutput<WO, WS> =
  | {
    type: "output"
    value: WO
  }
  | MessageOutputUpdateState<WS>

export type UpdateWorkerState<WS> = <K extends keyof WS>(key: K, value: WS[K]) => void

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
  const updateState = <K extends keyof WS>(key: K, value: WS[K]): void => {
    // The following cast is unsafe.
    // For example, if
    //   type WS = { foo: string, bar: number }
    //   type K = "foo" | "bar"
    // then we have
    //   WS[K] = string | number
    // Hence this compiles:
    //   const key: "foo" | "bar" = "foo"
    //   updateState(key, 1)
    const output = {
      type: "updateState",
      key,
      value,
    } as unknown as MessageOutputUpdateState<WS>
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
