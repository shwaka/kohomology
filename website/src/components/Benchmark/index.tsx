import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import { BenchmarkData } from "./BenchmarkData"
import { getChartArgument, collectBenchesPerTestCase } from "./benchmark"

import "@benchmark/data"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController)

declare global {
  interface Window {
    BENCHMARK_DATA: BenchmarkData
  }
}

function Bench({ name, dataset }): JSX.Element {
  const arg = getChartArgument(name, dataset)
  return (
    <Chart {...arg}/>
  )
}

function Benchset({ name, benchset }): JSX.Element {
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

export function Benchmark(): JSX.Element {
  const benchmarkData = window.BENCHMARK_DATA
  const benchmarks = Object.keys(benchmarkData.entries).map(name => ({
    name,
    benchset: collectBenchesPerTestCase(benchmarkData.entries[name]),
  }))
  return (
    <div>
      {benchmarks.map((benchmark) => (
        <Benchset
          key={benchmark.name}
          {...benchmark}
        />
      ))}
    </div>
  )
}
