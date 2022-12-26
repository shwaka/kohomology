import benchmarkData from "@benchmark/benchmarkData.json"
import { Checkbox, FormControlLabel } from "@mui/material"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import React, { useState } from "react"
import { Chart } from "react-chartjs-2"
import { BenchmarkData } from "./BenchmarkData"
import { BenchmarkDataHandler, BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"
import { getChartProps } from "./getChartProps"
import { movingAverage } from "./movingAverage"
import { ConfigureFilterCommit, useFilterCommit } from "./useFilterCommit"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

// @ts-expect-error because "declare module" in benchmarkData.d.ts is not working (why?)
const bd: BenchmarkData = benchmarkData
const dataHandler = new BenchmarkDataHandler(bd)

function Bench(
  { name, dataset, filterCommit }: {
    name: string
    dataset: BenchWithCommit[]
    filterCommit: (commit: CommitWithDate) => boolean
  }
): JSX.Element {
  const arg = getChartProps({ name, dataset, dataHandler, filterCommit })
  return (
    <Chart {...arg}/>
  )
}

function Benchset(
  { benchset, filterCommit, weightArray }: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
    filterCommit: (commit: CommitWithDate) => boolean
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
            dataset={benchAverages}
            filterCommit={filterCommit}
          />
        )
      })}
    </div>
  )
}

export function BenchmarkChart(): JSX.Element {
  const [showMovingAverage, setShowMovingAverage] = useState(false)
  const { filterCommit, configureFilterCommitProps } = useFilterCommit(dataHandler.commits)
  const weightArray = showMovingAverage ? [5, 4, 3, 2, 1] : [1]
  return (
    <div>
      <ConfigureFilterCommit {...configureFilterCommitProps}/>
      <FormControlLabel
        control={
          <Checkbox
            checked={showMovingAverage}
            onChange={(event) => setShowMovingAverage(event.target.checked)}
          />
        }
        label="Show moving average"
      />
      {dataHandler.benchsetsWithNames.map((benchsetWithName) => (
        <Benchset
          key={benchsetWithName.name}
          filterCommit={filterCommit}
          weightArray={weightArray}
          {...benchsetWithName}
        />
      ))}
    </div>
  )
}
