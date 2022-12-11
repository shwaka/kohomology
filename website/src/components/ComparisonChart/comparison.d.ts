import { Tool } from "./tools"

declare module "*/comparison.json" {
  interface Data {
    version: string
    benchmark_result: any
  }

  const value: { [K in Tool]: Data }
  export = value
}
