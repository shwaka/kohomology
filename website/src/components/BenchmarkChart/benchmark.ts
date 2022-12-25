// Copied from index.html generated by github-action-benchmark

import { ChartData } from "chart.js"
import { ChartProps } from "react-chartjs-2"
import { Bench, Benchmark, Commit, Tool } from "./BenchmarkData"

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

export interface BenchWithCommit {
  commit: Commit
  date: number
  tool: Tool
  bench: Bench
}

export function collectBenchesPerTestCase(entries: Benchmark[]): Map<string, BenchWithCommit[]> {
  const map: Map<string, BenchWithCommit[]> = new Map()
  for (const entry of entries) {
    const {commit, date, tool, benches} = entry
    for (const bench of benches) {
      const result: BenchWithCommit = { commit, date, tool, bench }
      const arr = map.get(bench.name)
      if (arr === undefined) {
        map.set(bench.name, [result])
      } else {
        arr.push(result)
      }
    }
  }
  return map
}

function extractMethodName(name: string): string {
  return name.replace("com.github.shwaka.kohomology.profile.KohomologyBenchmark.", "")
}

export function getChartProps(
  { name, dataset, getLabel }: {
    name: string
    dataset: BenchWithCommit[]
    getLabel: (benchWithCommit: BenchWithCommit) => string
  }
): ChartProps<"line", number[], string> {
  const color = toolColors[dataset.length > 0 ? dataset[0].tool : "_"]
  const data: ChartData<"line", number[], string> = {
    labels: dataset.map(getLabel),
    datasets: [
      {
        label: extractMethodName(name),
        data: dataset.map(d => d.bench.value),
        borderColor: color,
        backgroundColor: color + "60", // Add alpha for #rrggbbaa
        fill: true,
        tension: 0.2,
      }
    ],
  }
  const options: ChartProps<"line", number[], string>["options"] = {
    scales: {
      x: {
        title: {
          display: true,
          text: "commit date",
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
