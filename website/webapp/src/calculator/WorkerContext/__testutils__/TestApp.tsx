import React from "react"

import { myWorkerContext } from "./myWorkerContext"
import { MyComponent } from "../__playground__/MyComponent"

export function TestApp(): React.JSX.Element {
  return (
    <myWorkerContext.Provider
      defaultState={{ value: 0 }}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}
