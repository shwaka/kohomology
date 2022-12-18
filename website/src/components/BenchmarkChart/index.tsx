import benchmarkData from "@benchmark/benchmarkData.json"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import { BenchmarkData } from "./BenchmarkData"
import { getChartProps, collectBenchesPerTestCase, BenchWithCommit } from "./benchmark"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

declare global {
  interface Window {
    BENCHMARK_DATA: BenchmarkData
  }
}

function Bench(
  { name, dataset }: { name: string, dataset: BenchWithCommit[] }
): JSX.Element {
  const arg = getChartProps(name, dataset)
  return (
    <Chart {...arg}/>
  )
}

function Benchset(
  { benchset }: { name: string, benchset: Map<string, BenchWithCommit[]> }
): JSX.Element {
  return (
    <div>
      {Array.from(benchset.entries()).map(([benchName, benches]) => (
        <Bench
          key={benchName}
          name={benchName}
          dataset={benches}
        />
      ))}
    </div>
  )
}

export function BenchmarkChart(): JSX.Element {
  const benchsetsWithNames = Array
    .from(Object.entries(benchmarkData.entries))
    .map(([name, benchmarks]) => ({
      name,
      benchset: collectBenchesPerTestCase(benchmarks),
    }))
  return (
    <div>
      {benchsetsWithNames.map((benchsetWithName) => (
        <Benchset
          key={benchsetWithName.name}
          {...benchsetWithName}
        />
      ))}
    </div>
  )
}
