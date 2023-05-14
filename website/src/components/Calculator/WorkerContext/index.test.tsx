import { render, screen } from "@testing-library/react"
import React, { useEffect } from "react"
import { createWorkerContext } from "./WorkerContext"
import { useWorker } from "./useWorker"

interface MyWorkerInput {
  value: number
}

interface MyWorkerOutput {
  result: string
}

class MyWorker {
  value: number
  onmessage: (e: MessageEvent<MyWorkerOutput>) => void

  constructor() {
    this.value = 0
    this.onmessage = (_) => { throw new Error("MyWorker is not initialized") }
  }

  postMessage(input: MyWorkerInput): void {
    this.value += input.value
    const output: MyWorkerOutput = {
      result: `value=${this.value}`
    }
    this.onmessage({ data: output } as MessageEvent<MyWorkerOutput>)
  }
}

const myWorkerContext = createWorkerContext<MyWorkerInput, MyWorkerOutput>()

function MyComponent(): JSX.Element {
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

function App(): JSX.Element {
  return (
    <myWorkerContext.Provider
      createWorker={() => new MyWorker() as unknown as Worker}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}

test("WorkerContext", () => {
  render(<App/>)
  const div = screen.getByTestId("my-component")
  expect(div).toContainHTML("value=3")
})
