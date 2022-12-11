import { Target, Tool } from "./comparisonKeys"

declare module "*/comparison.json" {
  interface Data {
    version: string
    benchmark: { [K in Target]: number[] }
  }

  const value: {
    targets: {
      [K in Target]: { degrees: number[] }
    }
    result: { [K in Tool]: Data }
  }
  export = value
}
