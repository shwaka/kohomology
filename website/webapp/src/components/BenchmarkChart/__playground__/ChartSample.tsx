import { ReactElement } from "react"

import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import { Chart } from "react-chartjs-2"

import { getChartProps } from "../getChartProps"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

type Windows = {
  name: string
  version: number
  url: string
}

const dataset: Windows[] = [
  {
    name: "Windows 95",
    version: 95,
    url: "https://en.wikipedia.org/wiki/Windows_95",
  },
  {
    name: "Windows 98",
    version: 98,
    url: "https://en.wikipedia.org/wiki/Windows_98",
  },
  {
    name: "Windows 2000",
    version: 2000,
    url: "https://en.wikipedia.org/wiki/Windows_2000",
  },
  {
    name: "Windows 7",
    version: 7,
    url: "https://en.wikipedia.org/wiki/Windows_7",
  },
  {
    name: "Windows 8",
    version: 8,
    url: "https://en.wikipedia.org/wiki/Windows_8",
  },
  {
    name: "Windows 10",
    version: 10,
    url: "https://en.wikipedia.org/wiki/Windows_10",
  },
  {
    name: "Windows 11",
    version: 11,
    url: "https://en.wikipedia.org/wiki/Windows_11",
  },
]

export function ChartSample(): ReactElement {
  const props = getChartProps({
    datasetLabel: "Windows",
    color: "#4488cc",
    xTitle: "version name",
    yTitle: "version number",
    dataset,
    getValue: (windows) => ({
      x: windows.name,
      y: windows.version,
    }),
    labels: ["Invalid name"].concat(dataset.map((windows) => windows.name)),
    labelToTick: (label) => `[${label}]`,
    tooltipCallbacks: ({
      title: (windows) => `title for ${windows.name}`,
      afterTitle: (windows) => `afterTitle for ${windows.name}`,
      label: (windows) => `label for ${windows.name}`,
      afterLabel: (windows) => `afterLabel for ${windows.name}`,
    }),
    getOnClickUrl: (windows) => windows.url,
  })
  return (
    <div style={{ maxWidth: "800px" }}>
      <Chart {...props} />
    </div>
  )
}
