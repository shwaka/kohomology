import React, { useCallback, useState } from "react"

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

function range(n: number): number[] {
  if (n < 0) {
    return []
  }
  return [...Array(n).keys()]
}

export function ShowErrorsSample(): React.JSX.Element {
  const { count, increment, decrement } = useCounter()
  const messages: MessageWithType[] = range(count).map((i) => ({
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
