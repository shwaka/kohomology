import CodeBlock from "@theme/CodeBlock"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData, Colors } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import comparisonData from "./comparison.json"
import { Target, tools, Tool } from "./comparisonKeys"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, Colors)

function getData(target: Target): ChartData<"line", number[], string> {
  return {
    labels: comparisonData.targets[target].degrees.map((degree) => degree.toString()),
    datasets: tools.map((tool) => ({
      label: tool,
      data: comparisonData.result[tool].benchmark[target],
    })),
  }
}

export function ComparisonChart(): JSX.Element {
  const data = getData("FreeLoopSpaceOf2Sphere")
  return (
    <Chart
      type="line"
      data={data}
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
