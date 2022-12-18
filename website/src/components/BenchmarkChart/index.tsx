import benchmarkData from "@benchmark/benchmarkData.json"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import { BenchmarkData } from "./BenchmarkData"
import { getChartProps, collectBenchesPerTestCase, BenchWithCommit } from "./benchmark"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

function Bench(
  { name, dataset }: { name: string, dataset: BenchWithCommit[] }
): JSX.Element {
  // const getLabel = (benchWithCommit: BenchWithCommit): string => (
  //   benchWithCommit.commit.id.slice(0,7)
  // )
  const getLabel = (benchWithCommit: BenchWithCommit): string => (
    // 2022-01-01T11:23:45+09:00 -> 2022-01-01
    benchWithCommit.commit.timestamp.slice(0, 10)
  )
  const arg = getChartProps({ name, dataset, getLabel })
  return (
    <Chart {...arg}/>
  )
}

function Benchset(
  { benchset, filterBench }: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
    filterBench: (benchWithCommit: BenchWithCommit) => boolean
  }
): JSX.Element {
  return (
    <div>
      {Array.from(benchset.entries()).map(([benchName, benches]) => (
        <Bench
          key={benchName}
          name={benchName}
          dataset={benches.filter(filterBench)}
        />
      ))}
    </div>
  )
}

export function BenchmarkChart(): JSX.Element {
  // @ts-expect-error because "declare module" in benchmarkData.d.ts is not working (why?)
  const bd: BenchmarkData = benchmarkData
  const benchsetsWithNames = Array
    .from(Object.entries(bd.entries))
    .map(([name, benchmarks]) => ({
      name,
      benchset: collectBenchesPerTestCase(benchmarks),
    }))
  const filterBench = (_benchWithCommit: BenchWithCommit): boolean => (
    true
    // benchWithCommit.date > new Date("2022.10.01").getTime()
  )
  return (
    <div>
      {benchsetsWithNames.map((benchsetWithName) => (
        <Benchset
          key={benchsetWithName.name}
          filterBench={filterBench}
          {...benchsetWithName}
        />
      ))}
    </div>
  )
}
