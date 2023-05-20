import React, { useEffect } from "react"
import { useWorker } from "../useWorker"
import { myWorkerContext } from "./myWorkerContext"

export function MyComponent(): JSX.Element {
  const { postMessage, addListener, workerOutputLog } = useWorker(myWorkerContext)

  useEffect(() => {
    addListener("MyComponent", (workerOutput) => console.log(workerOutput))
  })

  return (
    <div data-testid="my-component">
      <button
        onClick={() => postMessage({ value: 3 })}
        data-testid="add3"
      >
        Add 3
      </button>
      <div data-testid="show-workerOutputLog">
        {workerOutputLog.map((workerOutput, index) => (
          <div key={index}>{workerOutput.result}</div>
        ))}
      </div>
    </div>
  )
}
