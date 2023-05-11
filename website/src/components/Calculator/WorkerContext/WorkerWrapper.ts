export class WorkerWrapper<WI, WO> {
  private readonly onmessageFunctions: Map<string, (workerOutput: WO) => void> = new Map()
  private readonly onRestartFunctions: Map<string, () => void> = new Map()
  private worker: Worker
  public workerOutputLog: WO[] = []

  constructor(private createWorker: () => Worker) {
    this.worker = this.createWorker()
    this.worker.onmessage = (e: MessageEvent<WO>): void => this.onmessage(e.data)
  }

  subscribe(key: string, onmessage: (workerOutput: WO) => void): void {
    this.onmessageFunctions.set(key, onmessage)
    console.log(`subscribe: ${key}`)
  }

  unsubscribe(key: string): void {
    this.onmessageFunctions.delete(key)
  }

  subscribeRestart(key: string, onRestart: () => void): void {
    this.onRestartFunctions.set(key, onRestart)
    console.log(`subscribe restart: ${key}`)
  }

  unsubscribeRestart(key: string): void {
    this.onRestartFunctions.delete(key)
  }

  postMessage(workerInput: WI): void {
    this.worker.postMessage(workerInput)
  }

  onmessage(workerOutput: WO): void {
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
    this.worker.onmessage = (e: MessageEvent<WO>): void => this.onmessage(e.data)
    this.onRestartFunctions.forEach((func) => func())
  }

  static default<WI, WO>(): WorkerWrapper<WI, WO> {
    return new WorkerWrapper(() => {
      return {} as Worker // dummy object
      // throw new Error("The default WorkerWrapper is used")
    })
  }
}
