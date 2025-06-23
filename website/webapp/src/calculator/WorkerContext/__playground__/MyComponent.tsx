import { useEffect, useState, ReactElement } from "react";

import { MessageOutput } from "../expose"
import { useWorker } from "../useWorker"
import { myWorkerContext } from "./myWorkerContext"
import { MyWorkerFunc, MyWorkerOutput, MyWorkerState } from "./MyWorkerInterface"

function ShowWorkerOutputLog({ log, testid }: {
  log: MessageOutput<MyWorkerOutput, MyWorkerState, MyWorkerFunc>[]
  testid: string
}): ReactElement {
  return (
    <div data-testid={testid} style={{ border: "1px solid gray" }}>
      <div>Log of MessageOutput</div>
      {log.map((workerOutput, index) => (
        <div key={index}>
          <span>
            {workerOutput.type === "output" ? workerOutput.value.result : "no value"}
          </span>
          <span style={{ color: "gray" }}>
            {JSON.stringify(workerOutput)}
          </span>
        </div>
      ))}
    </div>
  )
}

function ShowWorkerOutputLogFromListener({ log, testid }: {
  log: MyWorkerOutput[]
  testid: string
}): ReactElement {
  return (
    <div data-testid={testid} style={{ border: "1px solid gray" }}>
      <div>Log of WorkerOutput from listner</div>
      {log.map((workerOutput, index) => (
        <div key={index}>
          {workerOutput.result}
        </div>
      ))}
    </div>
  )
}

export function MyComponent(): ReactElement {
  const { postMessage, addListener, workerOutputLog, state: { value }, runAsync } = useWorker(myWorkerContext)
  const [workerOutputLogFromListener, setWorkerOutputLogFromListener] = useState<MyWorkerOutput[]>([])
  const [runAsyncResult, setRunAsyncResult] = useState(0)

  useEffect(() => {
    addListener("MyComponent", (workerOutput) =>
      setWorkerOutputLogFromListener((previous) => [...previous, workerOutput])
    )
  }, [addListener, workerOutputLogFromListener, setWorkerOutputLogFromListener])

  return (
    <div data-testid="my-component">
      <div>
        {/* Test runAsync */}
        <button
          onClick={async () => {
            const result: number = await runAsync("add", [5])
            setRunAsyncResult(result)
          }}
          data-testid="runAsync-add5"
        >
          Add 5
        </button>
        <div data-testid="show-runAsyncResult">{`runAsyncResult=${runAsyncResult}`}</div>
      </div>
      <div data-testid="show-state-value">{`stateValue=${value}`}</div>
      <div>
        {/* Test postMessage and its output (MyWorkerOutput) */}
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
      </div>
    </div>
  )
}
