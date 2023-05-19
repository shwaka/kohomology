import { render, screen } from "@testing-library/react"
import React, { useEffect } from "react"
import { MyWorker } from "./__testutils__/MyWorker"
import { useWorker } from "./useWorker"
import { myWorkerContext } from "./__testutils__/myWorkerContext"

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
