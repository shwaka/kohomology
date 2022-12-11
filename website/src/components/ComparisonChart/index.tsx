import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData, Colors } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"
import comparisonData from "./comparison.json"
import { tools } from "./tools"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, Colors)

const data: ChartData<"line", number[], string> = {
  labels: comparisonData.degrees.map((degree) => degree.toString()),
  datasets: tools.map((tool) => ({
    label: tool,
    data: comparisonData.result[tool].benchmark_result,
  })),
}

export function ComparisonChart(): JSX.Element {
  return (
    <Chart
      type="line"
      data={data}
    />
  )
}
