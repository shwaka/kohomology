import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import { getChartArgument, collectBenchesPerTestCase } from "./benchmark"

import "@benchmark/data"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController)

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
  const data = window.BENCHMARK_DATA
  const benchmarks = Object.keys(data.entries).map(name => ({
    name,
    benchset: collectBenchesPerTestCase(data.entries[name]),
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
