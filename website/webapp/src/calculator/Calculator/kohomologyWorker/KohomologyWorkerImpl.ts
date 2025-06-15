import { fromString, StyledMessage } from "@calculator/styled/message"
import { CallbackData, UpdateWorkerState, WorkerImpl } from "@calculator/WorkerContext/expose"
import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import { FreeDGAWrapper, ValidationResult, validateIdealGeneratorString, validateIdealJson } from "kohomology-js"

import { toStyledMessage } from "./styled"
import { KohomologyWorkerInput, KohomologyWorkerOutput, TargetName, ShowCohomology, WorkerInfo, KohomologyWorkerState, KohomologyWorkerFunc } from "./workerInterface"

type KohomologyMessageHandlerArgs = CallbackData<KohomologyWorkerOutput, KohomologyWorkerState> & {
  log?: (...messages: unknown[]) => void
  error?: (...messages: unknown[]) => void
}

class KohomologyMessageHandler {
  private dgaWrapper: FreeDGAWrapper | null = null
  private log: (...messages: unknown[]) => void
  private error: (...messages: unknown[]) => void
  private readonly postMessage: (output: KohomologyWorkerOutput) => void
  private readonly updateState: UpdateWorkerState<KohomologyWorkerState>

  constructor({
    postWorkerOutput, updateState, log, error,
  }: KohomologyMessageHandlerArgs) {
    // this.onmessage = this.onmessage.bind(this)
    this.log = log ?? ((...messages) => console.log(...messages))
    this.error = error ?? ((...messages) => console.error(...messages))
    this.postMessage = (output: KohomologyWorkerOutput): void => {
      this.log("KohomologyWorkerOutput", output)
      postWorkerOutput(output)
    }
    this.updateState = (...args) => {
      this.log("updateState", ...args)
      updateState(...args)
    }

    this.log("new KohomologyMessageHandler()")
  }

  public onmessage(input: KohomologyWorkerInput): void {
    this.log("KohomologyWorkerInput", input)
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
        case "computeMinimalModel":
          this.computeMinimalModel(input.targetName, input.isomorphismUpTo)
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
      const output: KohomologyWorkerOutput = {
        command: "printMessages",
        messages: messages,
      }
      this.postMessage(output)
    } else {
      const output: KohomologyWorkerOutput = {
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

  private computeMinimalModel(targetName: TargetName, isomorphismUpTo: number): void {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    this.notifyInfo({ status: "computing", progress: null })
    const reportProgress = (
      currentIsomorphismUpTo: number,
      targetIsomorphismUpTo: number,
      currentNumberOfGenerators: number,
    ): void => {
      this.updateState("workerInfo", {
        status: "computing",
        progress: (targetIsomorphismUpTo !== 0)
          ? (currentIsomorphismUpTo / targetIsomorphismUpTo)
          : 1,
        message: `Completed degree ${currentIsomorphismUpTo}/${targetIsomorphismUpTo}, ` +
          `Generators: ${currentNumberOfGenerators}`,
      })
    }
    this.sendMessages(this.dgaWrapper.computeMinimalModel(targetName, isomorphismUpTo, reportProgress).map(toStyledMessage))
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

  public validateIdealGenerator(generator: string): true | string {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    const result: ValidationResult = validateIdealGeneratorString(this.dgaWrapper, generator)
    switch (result.type){
      case "success":
        return true
      case "error":
        return result.message
      default:
        throw new Error("This can't happen!")
    }
  }

  public validateIdealGeneratorArray(generatorArray: string[]): true | string {
    assertNotNull(this.dgaWrapper, "dgaWrapper is null")
    const json = JSON.stringify(generatorArray)
    const result: ValidationResult = validateIdealJson(this.dgaWrapper, json)
    switch (result.type){
      case "success":
        return true
      case "error":
        return result.message
      default:
        throw new Error("This can't happen!")
    }
  }
}

function assertNotNull<T>(value: T | null, errorMessage: string): asserts value is T {
  if (value === null) {
    throw new Error(errorMessage)
  }
}

export class KohomologyWorkerImpl implements WorkerImpl<KohomologyWorkerInput, KohomologyWorkerFunc> {
  messageHandler: KohomologyMessageHandler
  workerFunc: KohomologyWorkerFunc

  constructor(args: KohomologyMessageHandlerArgs) {
    this.messageHandler = new KohomologyMessageHandler(args)
    this.workerFunc = {
      validateIdealGenerator: (generator: string) =>
        this.messageHandler.validateIdealGenerator(generator),
      validateIdealGeneratorArray: (generatorArray: string[]) =>
        this.messageHandler.validateIdealGeneratorArray(generatorArray),
    }
  }

  onWorkerInput(input: KohomologyWorkerInput): void {
    this.messageHandler.onmessage(input)
  }
}
