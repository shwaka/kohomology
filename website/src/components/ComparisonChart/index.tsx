import CodeBlock from "@theme/CodeBlock"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData } from "chart.js"
import React from "react"
import { Chart, ChartProps } from "react-chartjs-2"
import { getBackgroundColor, getBorderColor } from "./colors"
import comparisonData from "./comparison.json"
import { Target, tools, Tool } from "./comparisonKeys"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController)

function zip<T1, T2>(array1: T1[], array2: T2[]): [T1, T2][] {
  return array1.map((t1, i) => [t1, array2[i]])
}

function getData(target: Target): ChartData<"scatter", { x: number, y: number }[], string> {
  return {
    datasets: tools.map((tool, i) => {
      const data: { time: number[], degrees: number[] } = comparisonData[tool].benchmark[target]
      return {
        label: tool,
        data: zip(data.degrees, data.time).map(([x, y]) => ({ x, y })),
        showLine: true,
        borderColor: getBorderColor(i),
        backgroundColor: getBackgroundColor(i),
      }
    }),
  }
}

export function ComparisonChart({ target }: { target: Target }): JSX.Element {
  const data = getData(target)
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
