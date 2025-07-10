import { useState, ReactElement, useEffect } from "react"

import { Slider } from "@mui/material"
import { range } from "remeda"

interface RangeSliderProps<T> {
  items: T[]
  indexRange: number[]
  setIndexRange: (indexRange: number[]) => void
  getLabel: (item: T, index: number) => string
}

export function RangeSlider<T>({ items, indexRange, setIndexRange, getLabel }: RangeSliderProps<T>): ReactElement {
  return (
    <Slider
      min={0}
      max={items.length - 1}
      value={indexRange}
      onChange={(_event, newValue: number | number[]) => {
        setIndexRange(newValue as number[])
      }}
      valueLabelDisplay="auto"
      valueLabelFormat={(index: number) => {
        const item = items[index]
        return getLabel(item, index)
      }}
    />
  )
}

interface UseRangeFilterReturnValue<T> {
  isSelected: (item: T) => boolean
  rangeSliderProps: RangeSliderProps<T>
}

export function useRangeFilter<T>(
  { items, getValue, getLabel }: {
    items: T[]
    getValue: (item: T) => number
    getLabel: (item: T, index: number) => string
  }
): UseRangeFilterReturnValue<T> {
  useEffect(() => {
    if (!isSorted(items.map(getValue))) {
      throw new Error("items is not sorted")
    }
  }, [items, getValue])
  const [indexRange, setIndexRange] = useState<number[]>([0, items.length - 1])
  const isSelected = (item: T): boolean => {
    const value = getValue(item)
    const startValue = getValue(items[indexRange[0]])
    const endValue = getValue(items[indexRange[1]])
    return (value >= startValue) && (value <= endValue)
  }
  return {
    isSelected,
    rangeSliderProps: {
      items,
      indexRange,
      setIndexRange,
      getLabel,
    }
  }
}

function isSorted<T>(items: T[]): boolean {
  for (const i of range(0, items.length - 1)) {
    if (items[i] >= items[i + 1]) {
      return false
    }
  }
  return true
}
