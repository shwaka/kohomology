import { createWorkerContext } from "@calculator/WorkerContext"

import { KohomologyWorkerFunc, KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState } from "./workerInterface"

const createWorker = (): Worker => new Worker(new URL("./kohomology.worker.ts", import.meta.url))
export const kohomologyWorkerContext = createWorkerContext<KohomologyWorkerInput, KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>(createWorker)
