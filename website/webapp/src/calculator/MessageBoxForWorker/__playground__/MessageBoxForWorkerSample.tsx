import React from "react"

import SampleWorker from "worker-loader!./sample.worker"

import { MessageBoxForWorker } from ".."
import { sampleWorkerContext } from "./sampleWorkerContext"

export function MessageBoxForWorkerSample(): React.JSX.Element {
  const createWorker = (): Worker => new SampleWorker()

  return (
    <sampleWorkerContext.Provider
      createWorker={createWorker}
      defaultState={{
        value: 0,
      }}
    >
      <MessageBoxForWorker context={sampleWorkerContext}/>
    </sampleWorkerContext.Provider>
  )
}
