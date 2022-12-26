import { Bench, Benchmark, BenchmarkData, Commit, Tool } from "./BenchmarkData"

export interface BenchWithCommit {
  commit: Commit
  date: number
  tool: Tool
  bench: Bench
}

export class BenchmarkHandler {
  benchsetsWithNames: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
  }[]

  constructor(benchmarkData: BenchmarkData) {
    this.benchsetsWithNames = Array
      .from(Object.entries(benchmarkData.entries))
      .map(([name, benchmarks]) => ({
        name,
        benchset: BenchmarkHandler.collectBenchesPerTestCase(benchmarks),
      }))
  }

  static collectBenchesPerTestCase(entries: Benchmark[]): Map<string, BenchWithCommit[]> {
    const map: Map<string, BenchWithCommit[]> = new Map()
    for (const entry of entries) {
      const {commit, date, tool, benches} = entry
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
}
