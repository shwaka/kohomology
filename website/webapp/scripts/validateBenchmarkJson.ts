import fs from "fs"
import path from "path"

import { benchmarkDataSchema } from "@components/BenchmarkChart/BenchmarkData"

function validate(filepath: string): void {
  const raw: string = fs.readFileSync(filepath, "utf-8")
  const obj = JSON.parse(raw)

  try {
    benchmarkDataSchema.parse(obj)
    console.log(`Validation success for ${filepath}`)
  } catch (e) {
    console.error(`Validation failure for ${filepath}`)
    console.error(e)
  }
}

for (const name of ["core", "website"]) {
  const filepath = path.join(__dirname, `../benchmark-data/${name}/dev/bench/benchmarkData.json`)
  validate(filepath)
}
