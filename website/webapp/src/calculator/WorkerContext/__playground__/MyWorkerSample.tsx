import React from "react"

import { MyComponent } from "./MyComponent"
import { myWorkerContext } from "./myWorkerContext"

export function MyWorkerSample(): React.JSX.Element {
  return (
    <myWorkerContext.Provider
      defaultState={{ value: 0 }}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}
