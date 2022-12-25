import { Box, Slider } from "@mui/material"
import React, { useState } from "react"
import { BenchmarkData, Commit } from "./BenchmarkData"
import { BenchWithCommit } from "./benchmark"

type CommitWithDate = Commit & { date: number }

function areSameCommits(commit1: CommitWithDate, commit2: CommitWithDate): boolean {
  return commit1.id === commit2.id
}

function containsCommit(commits: CommitWithDate[], commit: CommitWithDate): boolean {
  for (const c of commits) {
    if (areSameCommits(c, commit)) {
      return true
    }
  }
  return false
}

function getCommits(benchmarkData: BenchmarkData): CommitWithDate[] {
  const commits: CommitWithDate[] = []
  for (const [_name, benchmarks] of Object.entries(benchmarkData.entries)) {
    for (const benchmark of benchmarks) {
      const commit = { ...benchmark.commit, date: benchmark.date }
      if (!containsCommit(commits, commit)) {
        commits.push(commit)
      }
    }
  }
  return commits.sort((commit1, commit2) => commit1.date - commit2.date)
}

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

export function useFilterBench(benchmarkData: BenchmarkData): UseFilterBenchReturnValue {
  const commits = getCommits(benchmarkData)
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
