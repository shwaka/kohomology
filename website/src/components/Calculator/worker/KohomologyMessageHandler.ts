import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { FreeDGAWrapper } from "kohomology-js"
import { UpdateWorkerState } from "../WorkerContext/expose"
import { fromString, StyledMessage } from "../styled/message"
import { toStyledMessage } from "./styled"
import { WorkerInput, WorkerOutput, TargetName, ShowCohomology, WorkerInfo, WorkerState } from "./workerInterface"

export class KohomologyMessageHandler {
  private dgaWrapper: FreeDGAWrapper | null = null
  private log: (...messages: unknown[]) => void
  private error: (...messages: unknown[]) => void
  private readonly postMessage: (output: WorkerOutput) => void
  private readonly updateState: UpdateWorkerState<WorkerState>

  constructor(
    postMessage: (output: WorkerOutput) => void,
    updateState: UpdateWorkerState<WorkerState>,
    log?: (...messages: unknown[]) => void,
    error?: (...messages: unknown[]) => void,
  ) {
    // this.onmessage = this.onmessage.bind(this)
    this.log = log ?? ((...messages) => console.log(...messages))
    this.error = error ?? ((...messages) => console.error(...messages))
    this.postMessage = (output: WorkerOutput): void => {
      this.log("WorkerOutput", output)
      postMessage(output)
    }
    this.updateState = (...args) => {
      this.log("updateState", ...args)
      updateState(...args)
    }

    this.log("new KohomologyMessageHandler()")
  }

  public onmessage(input: WorkerInput): void {
    this.log("WorkerInput", input)
    this.notifyInfo({ status: "computing", progress: null })
    try {
      switch (input.command) {
        case "updateJson":
          this.updateJson(input.json)
          this.updateIdealJson("[]")
          this.showDgaInfo()
          this.showIdealInfo()
          break
        case "updateIdealJson":
          this.updateIdealJson(input.idealJson)
          this.showIdealInfo()
          break
        case "computeCohomology":
          this.computeCohomology(input.targetName, input.minDegree, input.maxDegree, input.showCohomology)
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
    } finally {
      this.notifyInfo({ status: "idle" })
    }
  }


  private updateJson(json: string): void {
    try {
      this.dgaWrapper = new FreeDGAWrapper(json)
      this.updateState("json", json)
    } catch (error: unknown) {
      this.dgaWrapper = null
      throw error
    }
  }

  private updateIdealJson(idealJson: string): void {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    this.dgaWrapper.setIdeal(idealJson)
    this.updateState("idealJson", idealJson)
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

  private notifyInfo(workerInfo: WorkerInfo): void {
    this.updateState("workerInfo", workerInfo)
  }

  private computeCohomology(
    targetName: TargetName,
    minDegree: number, maxDegree: number,
    showCohomology: ShowCohomology,
  ): void {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    this.sendMessages(toStyledMessage(
      this.dgaWrapper.computationHeader(targetName, minDegree, maxDegree)
    ))
    this.notifyInfo({ status: "computing", progress: 0 })
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
        this.notifyInfo({ status: "computing", progress })
        styledMessages = []
      }
    }
    this.sendMessages(styledMessages)
  }

  private computeCohomologyClass(targetName: TargetName, cocycleString: string, showBasis: boolean): void {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    this.notifyInfo({ status: "computing", progress: null })
    this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomologyClass(targetName, cocycleString, showBasis)))
  }

  private showDgaInfo(): void {
    if (this.dgaWrapper === null) {
      const message = "[Error] Your DGA contains errors. Please fix them."
      this.updateState("dgaInfo", [fromString("error", message)])
    } else {
      this.updateState("dgaInfo", this.dgaWrapper.dgaInfo().map(toStyledMessage))
    }
  }

  private showIdealInfo(): void {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    this.updateState("idealInfo", toStyledMessage(this.dgaWrapper.idealInfo()))
  }
}

function assertNotNull<T>(value: T | null, errorMessage: string): asserts value is T {
  if (value === null) {
    throw new Error(errorMessage)
  }
}
