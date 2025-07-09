import { ChartProps } from "react-chartjs-2"

import { BenchmarkDataHandler, BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"
import { Value, getChartProps } from "./getChartProps"

// Colors from https://github.com/github/linguist/blob/master/lib/linguist/languages.yml
const toolColors = {
  cargo: "#dea584",
  go: "#00add8",
  benchmarkjs: "#f1e05a",
  benchmarkluau: "#000080",
  pytest: "#3572a5",
  googlecpp: "#f34b7d",
  catch2: "#f34b7d",
  julia: "#a270ba",
  jmh: "#b07219",
  benchmarkdotnet: "#178600",
  customBiggerIsBetter: "#38ff38",
  customSmallerIsBetter: "#ff3838",
  _: "#333333",
}

function extractMethodName(name: string): string {
  return name.replace("com.github.shwaka.kohomology.profile.KohomologyBenchmark.", "")
}

export function getBenchmarkChartProps(
  { name, dataset, dataHandler, filterCommit }: {
    name: string
    dataset: BenchWithCommit[]
    dataHandler: BenchmarkDataHandler
    filterCommit: (commit: CommitWithDate) => boolean
  }
): ChartProps<"line", Value[], string> {
  const benchUnit: string = dataset.length > 0 ? dataset[0].bench.unit : ""
  const color = toolColors[dataset.length > 0 ? dataset[0].tool : "_"] // previously, filteredDataset.length was used for "color". Why?
  const filteredDataset =
    dataset.filter((benchWithCommit) => filterCommit(benchWithCommit.commit))
  const filteredCommits = dataHandler.commits.filter(filterCommit)
  return getChartProps<BenchWithCommit>({
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
    tooltipCallbacks: {
      title: (data) => data.commit.id,
      afterTitle: (data) => {
        const commit = data.commit
        return "\n" + commit.message + "\n\n" + commit.timestamp + " committed by @" + commit.committer.username + "\n"
      },
      label: (data) => {
        const { range, unit, value } = data.bench
        let result = value.toString() + " " + unit
        if (typeof range === "string") {
          result += " (" + range + ")"
        } else if (range !== undefined) {
          // See https://github.com/benchmark-action/github-action-benchmark
          throw new Error("range must be a string, but was ${range}")
        }
        return result
      },
      afterLabel: (data) => {
        const { extra } = data.bench
        if (extra === undefined) {
          return ""
        }
        if (typeof extra !== "string") {
          // See https://github.com/benchmark-action/github-action-benchmark
          throw new Error("extra must be a string, but was ${extra}")
        }
        return "\n" + extra
      }
    },
    getOnClickUrl: (benchWithCommit) => benchWithCommit.commit.url,
  })
}
