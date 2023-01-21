import { RefObject, DependencyList, useRef, useEffect } from "react"

export function useScrollToBottom(deps: DependencyList | undefined): RefObject<HTMLDivElement> {
  const scrollRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = (): void => {
    const target: HTMLDivElement | null = scrollRef.current
    if (target !== null && target.scrollTo !== undefined) {
      // target.scrollTo can be undefined in test environment
      setTimeout(() => {
        target.scrollTo({ top: target.scrollHeight, behavior: "smooth" })
      })
    }
  }

  // eslint-disable below disables the following warning:
  //   React Hook useEffect was passed a dependency list that is not an array literal.
  //   This means we can't statically verify whether you've passed the correct dependencies
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => { scrollToBottom() }, deps)

  return scrollRef
}
