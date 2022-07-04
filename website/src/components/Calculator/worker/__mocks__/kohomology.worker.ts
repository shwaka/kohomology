import { KohomologyMessageHandler } from "../KohomologyMessageHandler"
import { WorkerInput, WorkerOutput } from "../workerInterface"

// - workerApi.postMessage corresponds to messageHandler.onmessage
// - workerApi.onmessage corresponds to messageHandler.postMessage

const workerApi = {
  // The property workerApi.postMessage is defined below by using messageHandler.
  // But, due to workerApi.onmessage, the whole workerApi must be defined here (see below).
  postMessage(_: WorkerInput): void { return },
  // The property workerApi.onmessage will be set outside of this module
  // and used in the construtor of KohomologyMessageHandler.
  // Hence workerApi must be defined here (the beginning of this module).
  onmessage(_: MessageEvent<WorkerOutput>): void { return }
}

const messageHandler = new KohomologyMessageHandler((output) =>
  workerApi.onmessage({ data: output } as MessageEvent<WorkerOutput>)
)

workerApi.postMessage = (input) =>  {
  messageHandler.onmessage({ data: input } as MessageEvent<WorkerInput>)
}

export default workerApi
