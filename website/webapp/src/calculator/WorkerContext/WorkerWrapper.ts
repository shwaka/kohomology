import { isInJest } from "@site/src/utils/isInJest"

import { MessageInput, MessageOutput, WFBase } from "./expose"

export class WorkerWrapper<WI, WO, WS, WF extends WFBase> {
  private readonly onmessageFunctions: Map<string, (workerOutput: MessageOutput<WO, WS, WF>) => void> = new Map()
  private readonly onRestartFunctions: Map<string, () => void> = new Map()
  private worker: Worker
  public workerOutputLog: MessageOutput<WO, WS, WF>[] = []

  constructor(private createWorker: () => Worker) {
    this.worker = this.createWorker()
    this.worker.onmessage = (e: MessageEvent<MessageOutput<WO, WS, WF>>): void => this.onmessage(e.data)
  }

  private log(...args: unknown[]): void {
    if (!isInJest()) {
      // Don't run console.log in JEST
      console.log(...args)
    }
  }

  subscribe(key: string, onmessage: (workerOutput: MessageOutput<WO, WS, WF>) => void): void {
    this.onmessageFunctions.set(key, onmessage)
    this.log(`subscribe: ${key}`)
  }

  unsubscribe(key: string): void {
    this.onmessageFunctions.delete(key)
    this.log(`unsubscribe: ${key}`)
  }

  subscribeRestart(key: string, onRestart: () => void): void {
    this.onRestartFunctions.set(key, onRestart)
    this.log(`subscribe restart: ${key}`)
  }

  unsubscribeRestart(key: string): void {
    this.onRestartFunctions.delete(key)
  }

  postMessage(workerInput: MessageInput<WI, WF>): void {
    this.worker.postMessage(workerInput)
  }

  onmessage(workerOutput: MessageOutput<WO, WS, WF>): void {
    this.workerOutputLog = [...this.workerOutputLog, workerOutput]
    this.onmessageFunctions.forEach((func) => func(workerOutput))
  }

  terminate(): void {
    console.log("Worker terminated")
    this.worker.terminate()
  }

  restart(): void {
    this.worker.terminate()
    this.worker = this.createWorker()
    this.worker.onmessage = (e: MessageEvent<MessageOutput<WO, WS, WF>>): void => this.onmessage(e.data)
    this.onRestartFunctions.forEach((func) => func())
  }

  static default<WI, WO, WS, WF extends WFBase>(): WorkerWrapper<WI, WO, WS, WF> {
    return new WorkerWrapper(() => {
      return {} as Worker // dummy object
      // throw new Error("The default WorkerWrapper is used")
    })
  }
}
