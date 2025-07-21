import "@testing-library/jest-dom/vitest"

import { cleanup } from "@testing-library/react"
import { afterEach } from "vitest"
import { vi } from "vitest"

import { kohomologyWorkerContext as mockKohomologyWorkerContext } from "./src/calculator/Calculator/kohomologyWorker/__mocks__/kohomologyWorkerContext"
import * as kohomologyWorkerContextModule from "./src/calculator/Calculator/kohomologyWorker/kohomologyWorkerContext"
import { myWorkerContext as mockMyWorkerContext } from "./src/calculator/WorkerContext/__mocks__/myWorkerContext"
import * as myWorkerContextModule from "./src/calculator/WorkerContext/__playground__/myWorkerContext"

afterEach(() => {
  cleanup()
})

vi.spyOn(kohomologyWorkerContextModule, "kohomologyWorkerContext", "get").mockReturnValue(
  mockKohomologyWorkerContext
)

vi.spyOn(myWorkerContextModule, "myWorkerContext", "get").mockReturnValue(
  mockMyWorkerContext
)
