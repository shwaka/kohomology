import { ReactNode } from "react"

import { Value, getChartProps } from "@components/ChartUtil/getChartProps"
import { useTooltip } from "@components/ChartUtil/useTooltip"
import { ChartProps } from "react-chartjs-2"

import { BenchmarkDataHandler, BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"
import { TooltipContent } from "./TooltipContent"

// Colors from https://github.com/github/linguist/blob/master/lib/linguist/languages.yml
// const toolColors = {
//   cargo: "#dea584",
//   go: "#00add8",
//   benchmarkjs: "#f1e05a",
//   benchmarkluau: "#000080",
//   pytest: "#3572a5",
//   googlecpp: "#f34b7d",
//   catch2: "#f34b7d",
//   julia: "#a270ba",
//   jmh: "#b07219",
//   benchmarkdotnet: "#178600",
//   customBiggerIsBetter: "#38ff38",
//   customSmallerIsBetter: "#ff3838",
//   _: "#333333",
// } as const satisfies Record<string, `#${string}`>

function extractMethodName(name: string): string {
  return name.replace("com.github.shwaka.kohomology.profile.KohomologyBenchmark.", "")
}

type UseBenchmarkChartReturnValue = {
  chartProps: ChartProps<"line", Value[], string>
  renderTooltip: () => ReactNode
}

export function useBenchmarkChart(
  { name, dataset, dataHandler, isSelected, color }: {
    name: string
    dataset: BenchWithCommit[]
    dataHandler: BenchmarkDataHandler
    isSelected: (commit: CommitWithDate) => boolean
    color: `#${string}`
  }
): UseBenchmarkChartReturnValue {
  const benchUnit: string = dataset.length > 0 ? dataset[0].bench.unit : ""
  const filteredDataset =
    dataset.filter((benchWithCommit) => isSelected(benchWithCommit.commit))
  const filteredCommits = dataHandler.commits.filter(isSelected)
  const { onClick, renderTooltip } = useTooltip({
    dataset: filteredDataset,
    TooltipContent,
    globalData: dataHandler.commits.map((commit) => commit.id),
  })
  const chartProps = getChartProps<BenchWithCommit>({
    datasetLabel: extractMethodName(name),
    color,
    xTitle: "commit date",
    yTitle: benchUnit,
    dataset: filteredDataset,
    getValue: (benchWithCommit) => ({
      x: benchWithCommit.commit.id,
      y: benchWithCommit.bench.value,
    }),
    labels: filteredCommits.map((commitWithDate) => commitWithDate.id),
    labelToTick: (commitId) => {
      const timestamp: string = dataHandler.getCommitTimestamp(commitId)
      // 2022-01-01T11:23:45+09:00 -> 2022-01-01
      return timestamp.slice(0, 10)
    },
    onClick,
  })
  return { chartProps, renderTooltip }
}
