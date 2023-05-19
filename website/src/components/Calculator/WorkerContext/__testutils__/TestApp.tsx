import React from "react"
import { MyComponent } from "./MyComponent"
import { MyWorker } from "./MyWorker"
import { myWorkerContext } from "./myWorkerContext"

export function TestApp(): JSX.Element {
  return (
    <myWorkerContext.Provider
      createWorker={() => new MyWorker() as unknown as Worker}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}
