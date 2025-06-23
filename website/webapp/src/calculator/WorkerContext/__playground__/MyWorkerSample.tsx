import { ReactElement } from "react"

import { MyComponent } from "./MyComponent"
import { myWorkerContext } from "./myWorkerContext"

export function MyWorkerSample(): ReactElement {
  return (
    <myWorkerContext.Provider
      defaultState={{ value: 0 }}
    >
      <MyComponent/>
    </myWorkerContext.Provider>
  )
}
