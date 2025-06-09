import React from "react"

import { MyComponent } from "./MyComponent"
import { MyWorker } from "./MyWorker"
import { myWorkerContext } from "./myWorkerContext"

export function TestApp(): React.JSX.Element {
  return (
    <myWorkerContext.Provider
      createWorker={() => new MyWorker() as unknown as Worker}
      defaultState={{ value: 0 }}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}
