import { expose } from "@calculator/WorkerContext/expose"

import { KohomologyWorkerImpl } from "./KohomologyMessageHandler"
import { WorkerFunc, WorkerInput, WorkerOutput, WorkerState } from "./workerInterface"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose<WorkerInput, WorkerOutput, WorkerState, WorkerFunc>(
  ctx.postMessage.bind(ctx),
  ({ postWorkerOutput, updateState }) => {
    const workerImpl = new KohomologyWorkerImpl({ postWorkerOutput, updateState })
    return {
      onWorkerInput: workerImpl.onWorkerInput.bind(workerImpl),
      workerFunc: workerImpl.workerFunc,
    }
  }
)

onmessage = exposed.onmessage
