import CodeBlock from "@theme/CodeBlock"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData } from "chart.js"
import React from "react"
import { Chart, ChartProps } from "react-chartjs-2"
import comparisonData from "./comparison.json"
import { Target, tools, Tool } from "./comparisonKeys"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController)

function getData(target: Target): ChartData<"line", number[], string> {
  return {
    labels: comparisonData.targets[target].degrees.map((degree) => `n=${degree}`),
    datasets: tools.map((tool) => ({
      label: tool,
      data: comparisonData.result[tool].benchmark[target],
    })),
  }
}

export function ComparisonChart({ target }: { target: Target }): JSX.Element {
  const data = getData(target)
  const options: ChartProps<"line", number[], string>["options"] = {
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
      type="line"
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
      {comparisonData.result[tool].version}
    </CodeBlock>
  )
}
