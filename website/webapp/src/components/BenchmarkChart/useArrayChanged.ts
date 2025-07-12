import { useEffect, useRef } from "react"

import { isDeepEqual } from "remeda"

export function useArrayChanged<T>(array: T[], callbackOnChange: () => void): void {
  const prevArrayRef = useRef<T[] | null>(null)
  useEffect(() => {
    if (prevArrayRef.current !== null) {
      const prevArray: T[] = prevArrayRef.current
      if (!isDeepEqual(prevArray, array)) {
        callbackOnChange()
      }
    }
    prevArrayRef.current = array
  }, [prevArrayRef, array, callbackOnChange])
}
