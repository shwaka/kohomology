import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData } from "chart.js"
import React from "react"
import { Chart } from "react-chartjs-2"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController)

const data: ChartData<"line", number[], string> = {
  labels: ["foo", "bar", "baz"],
  datasets: [
    {
      label: "dataset 1",
      data: [1, 2, 3],
    },
    {
      label: "dataset 2",
      data: [2, 3, 4],
    },
  ]
}

export function ComparisonChart(): JSX.Element {
  return (
    <Chart
      type="line"
      data={data}
    />
  )
}
