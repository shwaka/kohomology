import { Slider } from "@mui/material"
import React, { useState } from "react"
import { BenchWithCommit } from "./benchmark"
import { BenchmarkData, Commit } from "./BenchmarkData"

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
  numberOfCommits: number
  commitIndexRange: number[]
  setCommitIndexRange: (commitIndexRange: number[]) => void
}

export function ConfigureFilterBench({ numberOfCommits, commitIndexRange, setCommitIndexRange }: ConfigureFilterBenchProps): JSX.Element {
  return (
    <Slider
      min={0}
      max={numberOfCommits - 1}
      value={commitIndexRange}
      onChange={(_event, newValue: number | number[]) => {
        setCommitIndexRange(newValue as number[])
      }}
      valueLabelDisplay={"auto"}
    />
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
      commitIndexRange,
      setCommitIndexRange,
      numberOfCommits: commits.length
    }
  }
}
