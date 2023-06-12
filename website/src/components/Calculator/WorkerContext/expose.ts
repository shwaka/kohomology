export interface CallbackData<WI, WO> {
  postWorkerOutput: (output: WO) => void
}

export interface WorkerImpl<WI, WO> {
  onWorkerInput: (input: WI) => void
}

export interface ExposedWorkerImpl<WI, WO> {
  onmessage: (event: MessageEvent<WI>) => void
}

export function expose<WI, WO>(
  postMessage: (output: WO) => void,
  getWorkerImpl: (callbackData: CallbackData<WI, WO>) => WorkerImpl<WI, WO>
): ExposedWorkerImpl<WI, WO> {
  const postWorkerOutput = (output: WO): void => {
    postMessage(output)
  }
  const workerImpl = getWorkerImpl({ postWorkerOutput })
  const onmessage = (event: MessageEvent<WI>): void => {
    workerImpl.onWorkerInput(event.data)
  }
  return {
    onmessage,
  }
}
