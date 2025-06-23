import React, { useCallback, useState, ReactElement } from "react"

import * as R from "remeda"

import { MessageWithType } from "../MessageWithType"
import { ShowErrors } from "../ShowErrors"

interface UseCounterReturnValue {
  count: number
  increment: () => void
  decrement: () => void
}

function useCounter(): UseCounterReturnValue {
  const [count, setCount] = useState(0)
  const increment = useCallback(() => {
    setCount((prevCount) => (prevCount + 1))
  }, [setCount])
  const decrement = useCallback(() => {
    setCount((prevCount) => (prevCount - 1))
  }, [setCount])
  return { count, increment, decrement }
}

export function ShowErrorsSample(): ReactElement {
  const { count, increment, decrement } = useCounter()
  const messages: MessageWithType[] = R.range(0, count).map((i) => ({
    message: `Message-${i}`,
    type: `type-${i}`,
  }))
  return (
    <div>
      <div>
        count: {count}
      </div>
      <div>
        <button onClick={increment}>increment</button>
        <button onClick={decrement}>decrement</button>
      </div>
      <ShowErrors messages={messages}/>
      <div>
        Text below error
      </div>
    </div>
  )
}
