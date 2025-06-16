import { useCallback, useMemo, useState } from "react"

import * as R from "remeda"

export interface UseTruncatedListOptions<T> {
  minWeight: number
  step: number
  getWeight?: (item: T) => number
}

type ShowCommand = "showMore" | "showLess" | "showAll" | "showMin"

type AvailableCommands = { [K in ShowCommand]: boolean }

export interface UseTruncatedListReturnValue<T> {
  visibleItems: T[]
  visibleCount: number
  isTruncated: boolean
  commands: { [K in ShowCommand]: () => void }
  availableCommands: AvailableCommands
}

export function useTruncatedList<T>(
  items: T[],
  { minWeight, step, getWeight = defaultGetWeight }: UseTruncatedListOptions<T>,
): UseTruncatedListReturnValue<T> {
  const [maxWeight, setMaxWeight] = useState(minWeight)

  const totalWeight = useMemo(() => (
    R.sumBy(items, getWeight)
  ), [items, getWeight])
  const visibleItems = useMemo(() => (
    takeUpToWeight(items, maxWeight, getWeight)
  ), [items, maxWeight, getWeight])

  const totalCount = items.length
  const visibleCount = visibleItems.length
  const isTruncated = (visibleCount < totalCount)

  const showMore = useCallback(() => {
    setMaxWeight((prevMaxWeight) => Math.min(prevMaxWeight + step, totalWeight))
  }, [step, totalWeight])

  const showLess = useCallback(() => {
    setMaxWeight((prevMaxWeight) => Math.min(prevMaxWeight - step, totalWeight))
  }, [step, totalWeight])

  const showAll = useCallback(() => {
    setMaxWeight(totalWeight)
  }, [totalWeight])

  const showMin = useCallback(() => {
    setMaxWeight(minWeight)
  }, [minWeight])

  const canShowMore = (maxWeight < totalWeight)
  const canShowLess = (maxWeight > minWeight)

  const availableCommands: AvailableCommands = {
    showMore: canShowMore,
    showLess: canShowLess,
    showAll: canShowMore,
    showMin: canShowLess,
  }

  return {
    visibleItems,
    visibleCount,
    isTruncated,
    commands: { showMore, showLess, showAll, showMin },
    availableCommands,
  }
}

function takeUpToWeight<T>(
  items: T[],
  maxWeight: number,
  getWeight: (item: T) => number,
): T[] {
  const result: T[] = []
  let weightSum = 0
  for (const item of items) {
    const weight = getWeight(item)
    if (weightSum + weight <= maxWeight) {
      result.push(item)
      weightSum += weight
    } else {
      break
    }
  }
  return result
}

function defaultGetWeight(_: unknown): number {
  return 1
}
