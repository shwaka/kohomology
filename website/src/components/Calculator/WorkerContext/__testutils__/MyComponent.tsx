import React, { useEffect, useState } from "react"
import { MessageOutput } from "../expose"
import { useWorker } from "../useWorker"
import { MyWorkerFunc, MyWorkerOutput, MyWorkerState } from "./MyWorker"
import { myWorkerContext } from "./myWorkerContext"

function ShowWorkerOutputLog({ log, testid }: {
  log: MessageOutput<MyWorkerOutput, MyWorkerState, MyWorkerFunc>[]
  testid: string
}): React.JSX.Element {
  return (
    <div data-testid={testid}>
      {log.map((workerOutput, index) => (
        <div key={index}>
          {workerOutput.type === "output" ? workerOutput.value.result : "no result"}
        </div>
      ))}
    </div>
  )
}

function ShowWorkerOutputLogFromListener({ log, testid }: {
  log: MyWorkerOutput[]
  testid: string
}): React.JSX.Element {
  return (
    <div data-testid={testid}>
      {log.map((workerOutput, index) => (
        <div key={index}>
          {workerOutput.result}
        </div>
      ))}
    </div>
  )
}

export function MyComponent(): React.JSX.Element {
  const { postMessage, addListener, workerOutputLog, state: { value } } = useWorker(myWorkerContext)
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
      <ShowWorkerOutputLogFromListener
        log={workerOutputLogFromListener}
        testid="show-log-from-listener"
      />
      <div data-testid="show-state-value">{`stateValue=${value}`}</div>
    </div>
  )
}
