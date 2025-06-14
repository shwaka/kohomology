import React from "react"

import { useWorker } from "@calculator/WorkerContext"

import { MessageBoxForWorker } from ".."
import { sampleWorkerContext } from "./sampleWorkerContext"

export function MessageBoxForWorkerSample(): React.JSX.Element {
  const createWorker = (): Worker => new Worker(new URL("./sample.worker.ts", import.meta.url))

  return (
    <sampleWorkerContext.Provider
      createWorker={createWorker}
      defaultState={{
        value: 0,
      }}
    >
      <WorkerForm/>
      <MessageBoxForWorker context={sampleWorkerContext}/>
    </sampleWorkerContext.Provider>
  )
}

function WorkerForm(): React.JSX.Element {
  const { postMessage } = useWorker(sampleWorkerContext)
  return (
    <div>
      <button onClick={() => postMessage({ value: 1 })}>
        Add 1
      </button>
    </div>
  )
}
