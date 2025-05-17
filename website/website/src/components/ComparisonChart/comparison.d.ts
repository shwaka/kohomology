import { Target, Tool } from "./comparisonKeys"

declare module "*/comparison.json" {
  interface Data {
    version: string
    benchmark: {
      [K in Target]: {
        time: number[]
        degrees: number[]
      }
    }
  }

  const value: { [K in Tool]: Data }
  export = value
}
