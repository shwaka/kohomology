import { useState, ReactElement } from "react"

import { Box, Slider } from "@mui/material"

import { CommitWithDate } from "./BenchmarkDataHandler"

interface ConfigureFilterCommitProps {
  commits: CommitWithDate[]
  commitIndexRange: number[]
  setCommitIndexRange: (commitIndexRange: number[]) => void
}

export function ConfigureFilterCommit({ commits, commitIndexRange, setCommitIndexRange }: ConfigureFilterCommitProps): ReactElement {
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
          const commitId: string = commit.id.slice(0, 7)
          const commitDate: string = commit.timestamp.slice(0, 10)
          return `[${commitIndex}] ${commitDate} (${commitId})`
        }}
      />
    </Box>
  )
}

interface UseFilterCommitReturnValue {
  filterCommit: (commit: CommitWithDate) => boolean
  configureFilterCommitProps: ConfigureFilterCommitProps
}

export function useFilterCommit(commits: CommitWithDate[]): UseFilterCommitReturnValue {
  const [commitIndexRange, setCommitIndexRange] = useState<number[]>([0, commits.length - 1])
  const filterCommit = (commit: CommitWithDate): boolean => {
    const startDate = commits[commitIndexRange[0]].date
    const endDate = commits[commitIndexRange[1]].date
    return (commit.date >= startDate) && (commit.date <= endDate)
    // benchWithCommit.date > new Date("2022.10.01").getTime()
  }
  return {
    filterCommit,
    configureFilterCommitProps: {
      commits,
      commitIndexRange,
      setCommitIndexRange,
    }
  }
}
