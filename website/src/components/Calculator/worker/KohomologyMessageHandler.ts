import { FreeDGAWrapper } from "kohomology-js"
import { fromString, StyledMessage, toStyledMessage } from "./styled"
import { WorkerInput, WorkerOutput, TargetName, ShowCohomology } from "./workerInterface"

export class KohomologyMessageHandler {
  private dgaWrapper: FreeDGAWrapper | null = null
  constructor(
    private readonly postMessage: (output: WorkerOutput) => void,
  ) {
    this.onmessage = this.onmessage.bind(this)
  }

  public onmessage(e: MessageEvent<WorkerInput>): void {
    console.log("Worker start")
    const input: WorkerInput = e.data
    console.log(input)
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
          assertUnreachable(input, "Invalid command")
      }
    } catch (error: unknown) {
      if (error instanceof Error) {
        this.sendMessages(fromString("error", error.message))
      }
      console.error(error)
    }
  }


  private updateJson(json: string): void {
    this.dgaWrapper = new FreeDGAWrapper(json)
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

  private computeCohomology(
    targetName: TargetName,
    minDegree: number, maxDegree: number,
    showCohomology: ShowCohomology,
  ): void {
    assertNotNull(this.dgaWrapper)
    this.sendMessages(toStyledMessage(this.dgaWrapper.computationHeader(targetName)))
    for (let degree = minDegree; degree <= maxDegree; degree++) {
      switch (showCohomology) {
        case "basis":
          this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomology(targetName, degree)))
          break
        case "dim":
          this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomologyDim(targetName, degree)))
          break
      }
    }
  }

  private computeCohomologyClass(targetName: TargetName, cocycleString: string, showBasis: boolean): void {
    assertNotNull(this.dgaWrapper)
    this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomologyClass(targetName, cocycleString, showBasis)))
  }

  private showDgaInfo(): void {
    assertNotNull(this.dgaWrapper)
    const output: WorkerOutput = {
      command: "showDgaInfo",
      messages: this.dgaWrapper.dgaInfo().map(toStyledMessage),
    }
    this.postMessage(output)
  }
}

function assertNotNull<T>(value: T | null): asserts value is T {
  if (value === null) {
    throw new Error("The given value is null.")
  }
}

function assertUnreachable(_: never, message: string): never {
  throw new Error(`This can't happen! (${message})`)
}
