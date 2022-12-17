import CodeBlock from "@theme/CodeBlock"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData, ScatterController } from "chart.js"
import React from "react"
import { Chart, ChartProps } from "react-chartjs-2"
import { getBackgroundColor, getBorderColor } from "./colors"
import comparisonData from "./comparison.json"
import { Target, tools, Tool } from "./comparisonKeys"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController)

function zip<T1, T2>(array1: T1[], array2: T2[]): [T1, T2][] {
  return array1.map((t1, i) => [t1, array2[i]])
}

function getData(
  target: Target, tool: Tool, borderColor: string, backgroundColor: string
): ChartData<"scatter", { x: number, y: number }[], string>["datasets"][number] {
  const dataForLine: { time: number[], degrees: number[] } = comparisonData[tool].benchmark[target]
  return {
    label: tool,
    data: zip(dataForLine.degrees, dataForLine.time).map(([x, y]) => ({ x, y })),
    showLine: true,
    borderColor: borderColor,
    backgroundColor: backgroundColor,
  }
}

function getDataForTarget(target: Target): ChartData<"scatter", { x: number, y: number }[], string> {
  const datasets = tools.map((tool, i) => getData(
    target, tool, getBorderColor(i), getBackgroundColor(i)
  ))
  return { datasets }
}

export function ComparisonChart({ target }: { target: Target }): JSX.Element {
  const data = getDataForTarget(target)
  const options: ChartProps<"scatter", { x: number, y: number }[], string>["options"] = {
    plugins: {
      title: {
        display: true,
        text: target
      }
    },
    scales: {
      y: {
        title: {
          display: true,
          text: "time (seconds)",
        }
      },
    }
  }
  return (
    <Chart
      type="scatter"
      data={data}
      options={options}
    />
  )
}

export function ShowVersion({ tool }: { tool: Tool }): JSX.Element {
  return (
    <CodeBlock
      language="shell-session"
      title={tool}
    >
      {comparisonData[tool].version}
    </CodeBlock>
  )
}
