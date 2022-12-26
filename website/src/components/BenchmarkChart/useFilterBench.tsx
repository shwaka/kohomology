import { Box, Slider } from "@mui/material"
import React, { useState } from "react"
import { BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"

interface ConfigureFilterBenchProps {
  commits: CommitWithDate[]
  commitIndexRange: number[]
  setCommitIndexRange: (commitIndexRange: number[]) => void
}

export function ConfigureFilterBench({ commits, commitIndexRange, setCommitIndexRange }: ConfigureFilterBenchProps): JSX.Element {
  return (
    <Box>
      {"Restrict commits:"}
      <Slider
        min={0}
        max={commits.length - 1}
        value={commitIndexRange}
        onChange={(_event, newValue: number | number[]) => {
          setCommitIndexRange(newValue as number[])
        }}
        valueLabelDisplay={"auto"}
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

interface UseFilterBenchReturnValue {
  filterBench: (benchWithCommit: BenchWithCommit) => boolean
  configureFilterBenchProps: ConfigureFilterBenchProps
}

export function useFilterBench(commits: CommitWithDate[]): UseFilterBenchReturnValue {
  const [commitIndexRange, setCommitIndexRange] = useState<number[]>([0, commits.length - 1])
  const filterBench = (benchWithCommit: BenchWithCommit): boolean => {
    const startDate = commits[commitIndexRange[0]].date
    const endDate = commits[commitIndexRange[1]].date
    return (benchWithCommit.date >= startDate) && (benchWithCommit.date <= endDate)
    // benchWithCommit.date > new Date("2022.10.01").getTime()
  }
  return {
    filterBench,
    configureFilterBenchProps: {
      commits,
      commitIndexRange,
      setCommitIndexRange,
    }
  }
}
