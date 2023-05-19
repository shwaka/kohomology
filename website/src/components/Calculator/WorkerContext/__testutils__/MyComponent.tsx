import React, { useEffect } from "react"
import { useWorker } from "../useWorker"
import { myWorkerContext } from "./myWorkerContext"

export function MyComponent(): JSX.Element {
  const { postMessage, addListener, workerOutputLog } = useWorker(myWorkerContext)

  useEffect(() => {
    addListener("MyComponent", (workerOutput) => console.log(workerOutput))
  })

  useEffect(() => {
    postMessage({ value: 3 })
  }, [postMessage])

  return (
    <div data-testid="my-component">
      {workerOutputLog.map((workerOutput, index) => (
        <div key={index}>{workerOutput.result}</div>
      ))}
    </div>
  )
}
