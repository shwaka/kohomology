import { RefObject, DependencyList, useRef, useEffect } from "react"

function isAtBottom(target: HTMLDivElement): boolean {
  // Check if target is scrolled to the bottom.
  // Even if scrolled to the bottom,
  //   target.scrollTop - (target.scrollHeight - target.clientHeight)
  // is 27 or 28 (why?).
  // So 50 is added here.
  return target.scrollTop + 50 >= target.scrollHeight - target.clientHeight
}

export function useScrollToBottom(deps: DependencyList | undefined): RefObject<HTMLDivElement | null> {
  const scrollRef = useRef<HTMLDivElement | null>(null)

  const scrollToBottom = (): void => {
    const target: HTMLDivElement | null = scrollRef.current
    if (target !== null && target.scrollTo !== undefined && isAtBottom(target)) {
      // target.scrollTo can be undefined in test environment.
      // Check isAtBottom before rerendering.
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
