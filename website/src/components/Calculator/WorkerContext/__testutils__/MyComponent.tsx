import React, { useEffect, useState } from "react"
import { useWorker } from "../useWorker"
import { MyWorkerOutput } from "./MyWorker"
import { myWorkerContext } from "./myWorkerContext"

function ShowWorkerOutputLog({ log, testid }: { log: MyWorkerOutput[], testid: string }): JSX.Element {
  return (
    <div data-testid={testid}>
      {log.map((workerOutput, index) => (
        <div key={index}>{workerOutput.result}</div>
      ))}
    </div>
  )
}

export function MyComponent(): JSX.Element {
  const { postMessage, addListener, workerOutputLog } = useWorker(myWorkerContext)
  const [workerOutputLogFromListener, setWorkerOutputLogFromListener] = useState<MyWorkerOutput[]>([])

  useEffect(() => {
    addListener("MyComponent", (workerOutput) =>
      setWorkerOutputLogFromListener((previous) => [...previous, workerOutput])
    )
  }, [addListener, workerOutputLogFromListener, setWorkerOutputLogFromListener])

  return (
    <div data-testid="my-component">
      <button
        onClick={() => postMessage({ value: 3 })}
        data-testid="add3"
      >
        Add 3
      </button>
      <ShowWorkerOutputLog log={workerOutputLog} testid="show-workerOutputLog"/>
      <ShowWorkerOutputLog log={workerOutputLogFromListener} testid="show-log-from-listener"/>
    </div>
  )
}
