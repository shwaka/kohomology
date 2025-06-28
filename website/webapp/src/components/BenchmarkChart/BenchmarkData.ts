export type Tool = "cargo" | "go" | "benchmarkjs" | "pytest" | "googlecpp" | "catch2" | "julia" | "jmh" | "benchmarkdotnet" | "benchmarkluau" | "customBiggerIsBetter" | "customSmallerIsBetter"

export interface Bench {
  name: string
  value: number
  unit: string
  range?: unknown
  extra?: unknown
}

export interface Commit {
  author: {
    email: string
    name: string
    username: string
  }
  committer: {
    email: string
    name: string
    username: string
  }
  distinct: boolean
  id: string
  message: string
  timestamp: string
  tree_id: string
  url: string
}

export interface Benchmark {
  commit: Commit
  date: number
  tool: Tool
  benches: Bench[]
}

export interface BenchmarkData {
  lastUpdate: number
  repoUrl: string
  entries: {
    Benchmark: Benchmark[]
  }
}
