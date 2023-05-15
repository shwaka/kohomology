import { useCallback, useRef } from "react"

type TimeoutId = ReturnType<typeof setTimeout>

type SetOverwritableTimeout = ((
  callback: (...args: unknown[]) => void,
  ms: number,
) => void)

export function useOverwritableTimeout(): SetOverwritableTimeout {
  const timeoutIdRef = useRef<TimeoutId | null>(null)
  const setOverwritableTimeout: SetOverwritableTimeout = useCallback((callback, ms) => {
    if (timeoutIdRef.current !== null) {
      clearTimeout(timeoutIdRef.current)
    }
    const timeoutId: TimeoutId = setTimeout(callback, ms)
    timeoutIdRef.current = timeoutId
  }, [])
  return setOverwritableTimeout
}
