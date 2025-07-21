import { expose } from "../expose"
import { getMyWorkerImpl } from "./MyWorkerImpl"

const ctx = self as unknown as Worker

const exposed = expose(ctx.postMessage.bind(ctx), getMyWorkerImpl)

onmessage = exposed.onmessage
