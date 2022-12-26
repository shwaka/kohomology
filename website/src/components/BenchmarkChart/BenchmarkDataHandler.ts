import { Bench, Benchmark, BenchmarkData, Commit, Tool } from "./BenchmarkData"

export type CommitWithDate = Commit & { date: number }

export interface BenchWithCommit {
  commit: CommitWithDate
  date: number
  tool: Tool
  bench: Bench
}

export class BenchmarkDataHandler {
  benchsetsWithNames: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
  }[]
  commits: CommitWithDate[]
  private commitMap: Map<string, CommitWithDate>

  constructor(benchmarkData: BenchmarkData) {
    this.benchsetsWithNames = Array
      .from(Object.entries(benchmarkData.entries))
      .map(([name, benchmarks]) => ({
        name,
        benchset: BenchmarkDataHandler.collectBenchesPerTestCase(benchmarks),
      }))
    this.commits = BenchmarkDataHandler.getCommits(benchmarkData)
    this.commitMap = new Map()
    for (const commit of this.commits) {
      this.commitMap.set(commit.id, commit)
    }
  }

  getCommitTimestamp(commitId: string): string {
    const commit: CommitWithDate | undefined = this.commitMap.get(commitId)
    if (commit === undefined) {
      throw new Error(`[Error] commit not found: ${commitId}`)
    }
    return commit.timestamp
  }

  private static collectBenchesPerTestCase(entries: Benchmark[]): Map<string, BenchWithCommit[]> {
    const map: Map<string, BenchWithCommit[]> = new Map()
    for (const entry of entries) {
      const { commit: commitWithoutDate, date, tool, benches } = entry
      const commit: CommitWithDate = { ...commitWithoutDate, date }
      for (const bench of benches) {
        const result: BenchWithCommit = { commit, date, tool, bench }
        const arr = map.get(bench.name)
        if (arr === undefined) {
          map.set(bench.name, [result])
        } else {
          arr.push(result)
        }
      }
    }
    return map
  }

  private static areSameCommits(commit1: CommitWithDate, commit2: CommitWithDate): boolean {
    return commit1.id === commit2.id
  }

  private static containsCommit(commits: CommitWithDate[], commit: CommitWithDate): boolean {
    for (const c of commits) {
      if (BenchmarkDataHandler.areSameCommits(c, commit)) {
        return true
      }
    }
    return false
  }

  private static getCommits(benchmarkData: BenchmarkData): CommitWithDate[] {
    const commits: CommitWithDate[] = []
    for (const [_name, benchmarks] of Object.entries(benchmarkData.entries)) {
      for (const benchmark of benchmarks) {
        const commit = { ...benchmark.commit, date: benchmark.date }
        if (!BenchmarkDataHandler.containsCommit(commits, commit)) {
          commits.push(commit)
        }
      }
    }
    return commits.sort((commit1, commit2) => commit1.date - commit2.date)
  }
}
