// Copied from index.html generated by github-action-benchmark

import { ChartData } from "chart.js"
import { ChartProps } from "react-chartjs-2"
import { BenchmarkDataHandler, BenchWithCommit, CommitWithDate } from "./BenchmarkDataHandler"

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

type Value = { x: string, y: number }

export function getChartProps(
  { name, dataset, dataHandler, filterCommit }: {
    name: string
    dataset: BenchWithCommit[]
    dataHandler: BenchmarkDataHandler
    filterCommit: (commit: CommitWithDate) => boolean
  }
): ChartProps<"line", Value[], string> {
  const filteredDataset = dataset.filter((benchWithCommit) => filterCommit(benchWithCommit.commit))
  const color = toolColors[filteredDataset.length > 0 ? filteredDataset[0].tool : "_"]
  const data: ChartData<"line", Value[], string> = {
    labels: dataHandler.commits.filter(filterCommit).map((commit) => commit.id),
    datasets: [
      {
        label: extractMethodName(name),
        data: filteredDataset.map(d => ({ x: d.commit.id, y: d.bench.value })),
        borderColor: color,
        backgroundColor: color + "60", // Add alpha for #rrggbbaa
        fill: true,
        tension: 0.2,
      }
    ],
  }
  const options: ChartProps<"line", Value[], string>["options"] = {
    scales: {
      x: {
        type: "category",
        title: {
          display: true,
          text: "commit date",
        },
        ticks: {
          callback: function (tickValue: string | number): string {
            if (typeof tickValue === "string") {
              throw new Error("This can't happen!")
            }
            const commitId: string = this.getLabelForValue(tickValue)
            const timestamp: string = dataHandler.getCommitTimestamp(commitId)
            // 2022-01-01T11:23:45+09:00 -> 2022-01-01
            return timestamp.slice(0, 10)
          }
        }
      },
      y: {
        title: {
          display: true,
          text: dataset.length > 0 ? dataset[0].bench.unit : "",
        },
        suggestedMin: 0,
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          title: (items) => {
            const {dataIndex} = items[0]
            const commit = dataset[dataIndex].commit
            return commit.id
          },
          afterTitle: (items) => {
            const {dataIndex} = items[0]
            const commit = dataset[dataIndex].commit
            return "\n" + commit.message + "\n\n" + commit.timestamp + " committed by @" + commit.committer.username + "\n"
          },
          label: (item) => {
            let label = item.label
            const { range, unit, value } = dataset[item.dataIndex].bench
            label = value.toString() + " " + unit
            if (range !== undefined) {
              label += " (" + range + ")"
            }
            return label
          },
          afterLabel: item => {
            const { extra } = dataset[item.dataIndex].bench
            return (extra !== undefined) ? "\n" + extra : ""
          }
        }
      }
    },
    onClick: (_mouseEvent, activeElems) => {
      if (activeElems.length === 0) {
        return
      }
      const index = activeElems[0].index
      const url = dataset[index].commit.url
      window.open(url, "_blank")
    },
  }

  return {
    type: "line",
    data,
    options,
  }
}
