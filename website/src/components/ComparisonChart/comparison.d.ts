import { Tool } from "./tools"

declare module "*/comparison.json" {
  interface Data {
    version: string
    benchmark_result: number[]
  }

  const value: {
    degrees: number[]
    result: { [K in Tool]: Data }
  }
  export = value
}
