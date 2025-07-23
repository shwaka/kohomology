import { useEffect, useRef } from "react"

import { isDeepEqual } from "remeda"

export function useArrayChanged<T>(array: T[], callbackOnChange: () => void): void {
  // Use useRef instead of useState since there is no need to re-render
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
