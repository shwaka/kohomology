import { useRef, useState, ReactElement } from "react"

import benchmarkData from "@benchmark/benchmarkData.json"
import benchmarkDataWebsite from "@benchmark-website/benchmarkData.json"
import { useColorMode } from "@docusaurus/theme-common"
import { Box, Checkbox, FormControlLabel } from "@mui/material"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import { Chart } from "react-chartjs-2"

import { BenchmarkData } from "./BenchmarkData"
import { BenchmarkDataHandler, BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"
import { getChartProps } from "./getChartProps"
import { movingAverage } from "./movingAverage"
import { ConfigureFilterCommit, useFilterCommit } from "./useFilterCommit"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

function Bench(
  { name, dataset, filterCommit, dataHandler }: {
    name: string
    dataset: BenchWithCommit[]
    filterCommit: (commit: CommitWithDate) => boolean
    dataHandler: BenchmarkDataHandler
  }
): ReactElement {
  const arg = getChartProps({ name, dataset, dataHandler, filterCommit })
  return (
    <Chart {...arg} />
  )
}

function Benchset(
  { benchset, filterCommit, weightArray, dataHandler }: {
    name: string
    benchset: Map<string, BenchWithCommit[]>
    filterCommit: (commit: CommitWithDate) => boolean
    weightArray: number[]
    dataHandler: BenchmarkDataHandler
  }
): ReactElement {
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
            dataHandler={dataHandler}
          />
        )
      })}
    </div>
  )
}

function BenchmarkChartOf({ benchmarkData }: { benchmarkData: BenchmarkData }): ReactElement {
  const dataHandlerRef = useRef(new BenchmarkDataHandler(benchmarkData))
  const dataHandler = dataHandlerRef.current
  const [showMovingAverage, setShowMovingAverage] = useState(false)
  const { filterCommit, configureFilterCommitProps } = useFilterCommit(dataHandler.commits)
  const weightArray = showMovingAverage ? [5, 4, 3, 2, 1] : [1]
  const { colorMode } = useColorMode()
  const stickyBackgroundColor = (colorMode === "light") ? "white" : "var(--ifm-background-color)"
  return (
    <div>
      <Box sx={{ position: "sticky", top: "var(--ifm-navbar-height)", backgroundColor: stickyBackgroundColor }}>
        <ConfigureFilterCommit {...configureFilterCommitProps} />
      </Box>
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
          dataHandler={dataHandler}
          {...benchsetWithName}
        />
      ))}
    </div>
  )
}

export function BenchmarkChart(): ReactElement {
  // @ts-expect-error because "declare module" in benchmarkData.d.ts is not working (why?)
  const bd: BenchmarkData = benchmarkData
  // @ts-expect-error because "declare module" in benchmarkData.d.ts is not working (why?)
  const bdw: BenchmarkData = benchmarkDataWebsite

  return (
    <div>
      <BenchmarkChartOf benchmarkData={bd} />
      <BenchmarkChartOf benchmarkData={bdw} />
    </div>
  )
}
