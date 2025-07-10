import { useState, ReactElement } from "react"

import { Box, Slider } from "@mui/material"

interface ConfigureFilterCommitProps<T> {
  commits: T[]
  commitIndexRange: number[]
  setCommitIndexRange: (commitIndexRange: number[]) => void
  getLabel: (commit: T, index: number) => string
}

export function ConfigureFilterCommit<T>({ commits, commitIndexRange, setCommitIndexRange, getLabel }: ConfigureFilterCommitProps<T>): ReactElement {
  return (
    <Box>
      Restrict commits:
      <Slider
        min={0}
        max={commits.length - 1}
        value={commitIndexRange}
        onChange={(_event, newValue: number | number[]) => {
          setCommitIndexRange(newValue as number[])
        }}
        valueLabelDisplay="auto"
        valueLabelFormat={(commitIndex: number) => {
          const commit = commits[commitIndex]
          return getLabel(commit, commitIndex)
        }}
      />
    </Box>
  )
}

interface UseFilterCommitReturnValue<T> {
  filterCommit: (commit: T) => boolean
  configureFilterCommitProps: ConfigureFilterCommitProps<T>
}

export function useFilterCommit<T>(
  commits: T[],
  getValue: (commit: T) => number,
  getLabel: (commit: T, index: number) => string,
): UseFilterCommitReturnValue<T> {
  const [commitIndexRange, setCommitIndexRange] = useState<number[]>([0, commits.length - 1])
  const filterCommit = (commit: T): boolean => {
    const currentDate = getValue(commit)
    const startDate = getValue(commits[commitIndexRange[0]])
    const endDate = getValue(commits[commitIndexRange[1]])
    return (currentDate >= startDate) && (currentDate <= endDate)
    // benchWithCommit.date > new Date("2022.10.01").getTime()
  }
  return {
    filterCommit,
    configureFilterCommitProps: {
      commits,
      commitIndexRange,
      setCommitIndexRange,
      getLabel,
    }
  }
}
