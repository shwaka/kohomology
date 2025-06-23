import React, { ReactElement } from "react"

import { MessageOutput } from "@calculator/WorkerContext/expose"
import { useLocation } from "@docusaurus/router"
import { act, render, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

import { Calculator } from "."
import { InputJson } from "./__testutils__/InputJson"
import { expectComputeCohomologyButtonToContain, waitForInitialState, getComputeCohomologyButton, selectComputationTarget } from "./__testutils__/utilsOnCalculator"
import { KohomologyWorkerFunc, KohomologyWorkerOutput, KohomologyWorkerState } from "./kohomologyWorker/workerInterface"

const mockUseLocation = useLocation as unknown as jest.Mock
mockUseLocation.mockReturnValue({
  search: ""
})

class OnmessageCapturer {
  private queue: [(workerOutput: MessageOutput<KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>) => void, MessageOutput<KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>][]
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

  add(onmessage: (workerOutput: MessageOutput<KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>) => void, workerOutput: MessageOutput<KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>): void {
    if (this.enabled) {
      this.queue.push([onmessage, workerOutput])
    } else {
      onmessage(workerOutput)
    }
  }

  async waitForMessage(): Promise<void> {
    await waitFor(() => {
      expect(this.queue).not.toBeEmpty()
    })
  }
}

const capturer = new OnmessageCapturer()

jest.mock("@calculator/WorkerContext/WorkerWrapper", () => {
  const originalModule = jest.requireActual<typeof import("@calculator/WorkerContext/WorkerWrapper")>("@calculator/WorkerContext/WorkerWrapper")
  const OriginalWorkerWrapper = originalModule.WorkerWrapper
  const originalOnmessage = OriginalWorkerWrapper.prototype.onmessage

  // TODO: copy the object OriginalWorkerWrapper with its prototype
  OriginalWorkerWrapper.prototype.onmessage = function(workerOutput: MessageOutput<KohomologyWorkerOutput, KohomologyWorkerState, KohomologyWorkerFunc>): void {
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
    const user = userEvent.setup()
    render(<Calculator/>)
    await waitForInitialState()
    const computeCohomologyButton = getComputeCohomologyButton()
    expectComputeCohomologyButtonToContain("Compute")
    await user.click(computeCohomologyButton)
    expectComputeCohomologyButtonToContain("Compute")
  })

  it("should be 'computing' during the computation", async () => {
    const user = userEvent.setup()
    capturer.enable()

    // initialize
    render(<Calculator/>)
    await capturer.waitForMessage()
    capturer.popAll()
    await waitForInitialState()
    expectComputeCohomologyButtonToContain("Compute")

    // click the button
    const computeCohomologyButton = getComputeCohomologyButton()
    await user.click(computeCohomologyButton)
    await capturer.waitForMessage()
    capturer.pop() // pop "notifyInfo"
    expectComputeCohomologyButtonToContain("Computing")
    capturer.popAll()
    expectComputeCohomologyButtonToContain("Compute")
  })

  it("should be 'compute' after computation finished with an error", async () => {
    const user = userEvent.setup()
    render(<Calculator/>)
    await waitForInitialState()

    // This causes an error since deg(sx) is zero.
    const json = '[["x", 1, "zero"]]'
    await InputJson.inputValidJson(user, json)
    await selectComputationTarget(user, "freeLoopSpace")

    const computeCohomologyButton = getComputeCohomologyButton()
    expectComputeCohomologyButtonToContain("Compute")
    await user.click(computeCohomologyButton)
    expectComputeCohomologyButtonToContain("Compute")
  })
})
