import { useLocation } from "@docusaurus/router"
import { act, fireEvent, render, waitFor } from "@testing-library/react"
import React from "react"
import { MessageOutput } from "./WorkerContext/expose"
import { InputJson } from "./__testutils__/InputJson"
import { expectComputeCohomologyButtonToContain, expectInitialState, getComputeCohomologyButton, selectComputationTarget } from "./__testutils__/utilsOnCalculator"
import { WorkerFunc, WorkerOutput, WorkerState } from "./worker/workerInterface"
import { Calculator } from "."

const mockUseLocation = useLocation as unknown as jest.Mock
mockUseLocation.mockReturnValue({
  search: ""
})

class OnmessageCapturer {
  queue: [(workerOutput: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>) => void, MessageOutput<WorkerOutput, WorkerState, WorkerFunc>][]
  enabled: boolean

  constructor() {
    this.queue = []
    this.enabled = false
  }

  initialize(): void {
    this.disable()
    this.queue = []
  }

  enable(): void {
    this.enabled = true
  }

  disable(): void {
    this.enabled = false
  }

  pop(): void {
    if (!this.enabled) {
      throw new Error("enabled is false")
    }
    const firstElement = this.queue.shift()
    if (firstElement === undefined) {
      throw new Error("queue is empty")
    }
    const [onmessage, workerOutput] = firstElement
    act(() => onmessage(workerOutput))
  }

  popAll(): void {
    while (this.queue.length > 0) {
      this.pop()
    }
  }

  add(onmessage: (workerOutput: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>) => void, workerOutput: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>): void {
    if (this.enabled) {
      this.queue.push([onmessage, workerOutput])
    } else {
      onmessage(workerOutput)
    }
  }
}

const capturer = new OnmessageCapturer()

jest.mock("./WorkerContext/WorkerWrapper", () => {
  const originalModule = jest.requireActual<typeof import("./WorkerContext/WorkerWrapper")>("./WorkerContext/WorkerWrapper")
  const OriginalWorkerWrapper = originalModule.WorkerWrapper
  const originalOnmessage = OriginalWorkerWrapper.prototype.onmessage

  // TODO: copy the object OriginalWorkerWrapper with its prototype
  OriginalWorkerWrapper.prototype.onmessage = function(workerOutput: MessageOutput<WorkerOutput, WorkerState, WorkerFunc>): void {
    capturer.add(originalOnmessage.bind(this), workerOutput)
  }

  return {
    ...originalModule,
    WorkerWrapper: OriginalWorkerWrapper,
  }
})

describe("text on the 'compute' button", () => {
  beforeEach(() => {
    capturer.initialize()
  })

  it("should be 'compute' after computation finished", async () => {
    render(<Calculator/>)
    expectInitialState()
    const computeCohomologyButton = getComputeCohomologyButton()
    expectComputeCohomologyButtonToContain("Compute")
    fireEvent.click(computeCohomologyButton)
    expectComputeCohomologyButtonToContain("Compute")
  })

  it("should be 'computing' during the computation", async () => {
    capturer.enable()

    // initialize
    render(<Calculator/>)
    expectInitialState()
    capturer.popAll()
    expectComputeCohomologyButtonToContain("Compute")

    // click the button
    const computeCohomologyButton = getComputeCohomologyButton()
    fireEvent.click(computeCohomologyButton)
    capturer.pop() // pop "notifyInfo"
    expectComputeCohomologyButtonToContain("Computing")
    capturer.popAll()
    expectComputeCohomologyButtonToContain("Compute")
  })

  it("should be 'compute' after computation finished with an error", async () => {
    render(<Calculator/>)
    expectInitialState()

    // This causes an error since deg(sx) is zero.
    const json = '[["x", 1, "zero"]]'
    await InputJson.inputValidJson(json)
    selectComputationTarget("freeLoopSpace")

    const computeCohomologyButton = getComputeCohomologyButton()
    expectComputeCohomologyButtonToContain("Compute")
    fireEvent.click(computeCohomologyButton)
    expectComputeCohomologyButtonToContain("Compute")
  })
})
