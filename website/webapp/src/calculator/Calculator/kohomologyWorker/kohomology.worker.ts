import { expose } from "@calculator/WorkerContext/expose"

import { getKohomologyWorkerImpl } from "./KohomologyWorkerImpl"

const ctx = self as unknown as Worker

const exposed = expose(ctx.postMessage.bind(ctx), getKohomologyWorkerImpl)

onmessage = exposed.onmessage
