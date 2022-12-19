import benchmarkData from "@benchmark/benchmarkData.json"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import React, { useState } from "react"
import { Chart } from "react-chartjs-2"
import { BenchmarkData } from "./BenchmarkData"
import { getChartProps, collectBenchesPerTestCase, BenchWithCommit } from "./benchmark"
import { movingAverage } from "./movingAverage"
import { Checkbox, FormControlLabel } from "@mui/material"

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
  { benchset, filterBench, weightArray }: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
    filterBench: (benchWithCommit: BenchWithCommit) => boolean
    weightArray: number[]
  }
): JSX.Element {
  const getValue = (bench: BenchWithCommit): number => bench.bench.value
  const getDataWithNewValue = (bench: BenchWithCommit, newValue: number): BenchWithCommit => ({
    ...bench,
    bench: {
      ...bench.bench,
      value: newValue,
    }
  })
  return (
    <div>
      {Array.from(benchset.entries()).map(([benchName, benches]) => {
        const benchAverages = movingAverage({
          dataArray: benches,
          weightArray,
          getValue,
          getDataWithNewValue
        })
        return (
          <Bench
            key={benchName}
            name={benchName}
            dataset={benchAverages.filter(filterBench)}
          />
        )
      })}
    </div>
  )
}

export function BenchmarkChart(): JSX.Element {
  const [showMovingAverage, setShowMovingAverage] = useState(false)
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
  const weightArray = showMovingAverage ? [5, 4, 3, 2, 1] : [1]
  return (
    <div>
      <FormControlLabel
        control={
          <Checkbox
            checked={showMovingAverage}
            onChange={(event) => setShowMovingAverage(event.target.checked)}
          />
        }
        label="Show moving average"
      />
      {benchsetsWithNames.map((benchsetWithName) => (
        <Benchset
          key={benchsetWithName.name}
          filterBench={filterBench}
          weightArray={weightArray}
          {...benchsetWithName}
        />
      ))}
    </div>
  )
}
