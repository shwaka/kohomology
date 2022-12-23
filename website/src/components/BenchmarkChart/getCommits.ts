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

export function getCommits(benchmarkData: BenchmarkData): Commit[] {
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
