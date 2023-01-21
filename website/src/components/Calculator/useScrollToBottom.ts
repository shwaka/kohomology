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
  useEffect(() => { scrollToBottom() }, deps)

  return scrollRef
}
