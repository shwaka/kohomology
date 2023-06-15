import { useCallback, useState } from "react"

interface UseMutableArrayReturnValue<T> {
  array: T[]
  push: (value: T) => void
}

export function useMutableArray<T>(): UseMutableArrayReturnValue<T> {
  const [array, setArray] = useState<T[]>([])

  const push = useCallback((value: T): void => {
    setArray((current) => [...current, value])
  }, [setArray])

  return {
    array, push,
  }
}
