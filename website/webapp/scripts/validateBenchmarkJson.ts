import fs from "fs"
import path from "path"

import { benchmarkDataSchema } from "@components/BenchmarkChart/BenchmarkData"

function validate(filepath: string): void {
  const raw: string = fs.readFileSync(filepath, "utf-8")
  const obj = JSON.parse(raw)

  try {
    benchmarkDataSchema.parse(obj)
    console.log("Validation success")
  } catch (e) {
    console.error("Validation failure")
    throw e
  }
}

const filepath = path.join(__dirname, "../benchmark-data/core/dev/bench/benchmarkData.json")
validate(filepath)
