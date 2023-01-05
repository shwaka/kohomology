import { FreeDGAWrapper } from "kohomology-js"
import { fromString, StyledMessage } from "../styled/message"
import { toStyledMessage } from "./styled"
import { WorkerInput, WorkerOutput, TargetName, ShowCohomology, WorkerStatusWithProgress } from "./workerInterface"

export class KohomologyMessageHandler {
  private dgaWrapper: FreeDGAWrapper | null = null
  private log: (message: unknown) => void
  private error: (message: unknown) => void
  constructor(
    private readonly postMessage: (output: WorkerOutput) => void,
    log?: (message: unknown) => void,
    error?: (message: unknown) => void,
  ) {
    // this.onmessage = this.onmessage.bind(this)
    this.log = log ?? ((message) => console.log(message))
    this.error = error ?? ((message) => console.error(message))
  }

  public onmessage(input: WorkerInput): void {
    this.log("Worker start")
    this.log(input)
    try {
      switch (input.command) {
        case "updateJson":
          this.updateJson(input.json)
          break
        case "computeCohomology":
          this.computeCohomology(input.targetName, input.minDegree, input.maxDegree, input.showCohomology)
          break
        case "dgaInfo":
          this.showDgaInfo()
          break
        case "computeCohomologyClass":
          this.computeCohomologyClass(input.targetName, input.cocycleString, input.showBasis)
          break
        default:
          throw new ExhaustivityError(input, `Invalid command: ${input}`)
      }
    } catch (error: unknown) {
      if (error instanceof Error) {
        this.sendMessages(fromString("error", error.message))
      }
      this.error(error)
    }
  }


  private updateJson(json: string): void {
    try {
      this.dgaWrapper = new FreeDGAWrapper(json)
    } catch (error: unknown) {
      this.dgaWrapper = null
      throw error
    }
  }

  private sendMessages(messages: StyledMessage | StyledMessage[]): void {
    if (messages instanceof Array) {
      const output: WorkerOutput = {
        command: "printMessages",
        messages: messages,
      }
      this.postMessage(output)
    } else {
      const output: WorkerOutput = {
        command: "printMessages",
        messages: [messages],
      }
      this.postMessage(output)
    }
  }

  private notifyProgress(statusWithProgress: WorkerStatusWithProgress): void {
    const output: WorkerOutput = {
      command: "notifyProgress",
      ...statusWithProgress,
    }
    this.postMessage(output)
  }

  private computeCohomology(
    targetName: TargetName,
    minDegree: number, maxDegree: number,
    showCohomology: ShowCohomology,
  ): void {
    assertNotNull(this.dgaWrapper)
    this.sendMessages(toStyledMessage(
      this.dgaWrapper.computationHeader(targetName, minDegree, maxDegree)
    ))
    this.notifyProgress({ status: "computing", progress: 0 })
    let styledMessages: StyledMessage[] = []
    let previousTime: number = new Date().getTime() // in millisecond
    for (let degree = minDegree; degree <= maxDegree; degree++) {
      switch (showCohomology) {
        // Don't send message immediately for performance reason.
        // If styledMessages.push(...) is replaced with this.sendMessages(...),
        // then the Calculator significantly slows down.
        // This is because this.sendMessages(...) causes re-render of the component Calculator.
        case "basis":
          styledMessages.push(toStyledMessage(this.dgaWrapper.computeCohomology(targetName, degree)))

          break
        case "dim":
          styledMessages.push(toStyledMessage(this.dgaWrapper.computeCohomologyDim(targetName, degree)))
          break
      }
      const currentTime = new Date().getTime() // in millisecond
      if (currentTime - previousTime > 500) {
        previousTime = currentTime
        this.sendMessages(styledMessages)
        const progress = (degree - minDegree + 1) / (maxDegree - minDegree + 1)
        this.notifyProgress({ status: "computing", progress })
        styledMessages = []
      }
    }
    this.sendMessages(styledMessages)
    this.notifyProgress({ status: "idle" })
  }

  private computeCohomologyClass(targetName: TargetName, cocycleString: string, showBasis: boolean): void {
    assertNotNull(this.dgaWrapper)
    this.notifyProgress({ status: "computing", progress: null })
    this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomologyClass(targetName, cocycleString, showBasis)))
    this.notifyProgress({ status: "idle" })
  }

  private showDgaInfo(): void {
    if (this.dgaWrapper === null) {
      const message = "[Error] Your DGA contains errors. Please fix them."
      const output: WorkerOutput = {
        command: "showDgaInfo",
        messages: [fromString("error", message)],
      }
      this.postMessage(output)
    } else {
      const output: WorkerOutput = {
        command: "showDgaInfo",
        messages: this.dgaWrapper.dgaInfo().map(toStyledMessage),
      }
      this.postMessage(output)
    }
  }
}

function assertNotNull<T>(value: T | null): asserts value is T {
  if (value === null) {
    throw new Error("The given value is null.")
  }
}

class ExhaustivityError extends Error {
  // https://typescriptbook.jp/reference/statements/never#%E4%BE%8B%E5%A4%96%E3%81%AB%E3%82%88%E3%82%8B%E7%B6%B2%E7%BE%85%E6%80%A7%E3%83%81%E3%82%A7%E3%83%83%E3%82%AF (例外による網羅性チェック)
  constructor(value: never, message = `Unsupported type: ${value}`) {
    super(message)
  }
}
