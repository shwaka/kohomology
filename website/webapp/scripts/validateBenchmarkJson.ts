import fs from "fs"
import path from "path"

import { benchmarkDataSchema } from "@components/BenchmarkChart/benchmarkDataSchema"
import { z } from "zod/v4"

function validate(filepath: string): void {
  const raw: string = fs.readFileSync(filepath, "utf-8")
  const obj = JSON.parse(raw)

  const result = benchmarkDataSchema.safeParse(obj)
  if (result.success) {
    console.log(`Validation success for ${filepath}`)
  } else {
    console.error(`Validation failure for ${filepath}`)
    console.error(z.prettifyError(result.error))
    throw new Error(`Validation failure for ${filepath}`)
  }
}

for (const name of ["core", "website"]) {
  const filepath = path.join(__dirname, `../benchmark-data/${name}/dev/bench/benchmarkData.json`)
  validate(filepath)
}
