import { useCallback, useState } from "react"

export interface UseTruncatedListOptions {
  minCount: number
  step: number
}

type ShowCommand = "showMore" | "showLess" | "showAll" | "showMin"

type AvailableCommands = { [K in ShowCommand]: boolean }

export interface UseTruncatedListReturnValue<T> {
  visibleItems: T[]
  visibleCount: number
  commands: { [K in ShowCommand]: () => void }
  availableCommands: AvailableCommands
}

export function useTruncatedList<T>(
  items: T[],
  { minCount, step }: UseTruncatedListOptions,
): UseTruncatedListReturnValue<T> {
  const [visibleCount, setVisibleCount] = useState(minCount)

  const total = items.length
  const visibleItems = items.slice(0, visibleCount)

  const showMore = useCallback(() => {
    setVisibleCount((prevCount) => Math.min(prevCount + step, total))
  }, [step, total])

  const showLess = useCallback(() => {
    setVisibleCount((prevCount) => Math.max(prevCount - step, minCount))
  }, [step, minCount])

  const showAll = useCallback(() => {
    setVisibleCount(total)
  }, [total])

  const showMin = useCallback(() => {
    setVisibleCount(minCount)
  }, [minCount])

  const canShowMore = (visibleCount < total)
  const canShowLess = (visibleCount > minCount)

  const availableCommands: AvailableCommands = {
    showMore: canShowMore,
    showLess: canShowLess,
    showAll: canShowMore,
    showMin: canShowLess,
  }

  return {
    visibleItems,
    visibleCount,
    commands: { showMore, showLess, showAll, showMin },
    availableCommands,
  }
}
