import { useLocation } from "@docusaurus/router"
import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import React from "react"
import { clickComputeCohomologyButton, expectInitialState, expectResultsToContainHTML, getComputeCohomologyButton } from "./__testutils__/utilsOnCalculator"
import { Calculator } from "."
import { WorkerWrapper } from "./WorkerContext/WorkerWrapper"
import { WorkerOutput } from "./worker/workerInterface"

const mockUseLocation = useLocation as unknown as jest.Mock
mockUseLocation.mockReturnValue({
  search: ""
})

class OnmessageCapturer {
  queue: [(workerOutput: WorkerOutput) => void, WorkerOutput][]
  enabled: boolean

  constructor() {
    this.queue = []
    this.enabled = false
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
    onmessage(workerOutput)
  }

  add(onmessage: (workerOutput: WorkerOutput) => void, workerOutput: WorkerOutput): void {
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
  OriginalWorkerWrapper.prototype.onmessage = function(workerOutput: WorkerOutput): void {
    capturer.add(originalOnmessage.bind(this), workerOutput)
  }

  return {
    ...originalModule,
    WorkerWrapper: OriginalWorkerWrapper,
  }
})

describe("'computing' shown on the 'compute' button", () => {
  beforeEach(() => {
    capturer.disable()
  })

  it("disappears after computation finished", async () => {
    render(<Calculator/>)
    expectInitialState()
    const computeCohomologyButton = getComputeCohomologyButton()
    expect(computeCohomologyButton).toContainHTML("Compute")
    fireEvent.click(computeCohomologyButton)
    // expect(computeCohomologyButton).toContainHTML("Compute")
  })
})
