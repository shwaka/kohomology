import { BenchmarkData } from "./BenchmarkData"

declare module "*/benchmarkData.json" {
  const value: BenchmarkData
  export = value
}
