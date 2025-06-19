import { expose } from "@calculator/WorkerContext/expose"

import { getMyWorkerImpl } from "./MyWorkerImpl"

// eslint-disable-next-line no-restricted-globals
const ctx = self as unknown as Worker

const exposed = expose(ctx.postMessage.bind(ctx), getMyWorkerImpl)

onmessage = exposed.onmessage
