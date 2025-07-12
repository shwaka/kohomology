import { ReactElement } from "react"

import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler } from "chart.js"
import { Chart } from "react-chartjs-2"

import { getChartProps } from "../getChartProps"
import { RangeSlider, useRangeFilter } from "../useRangeFilter"
import { useTooltip } from "../useTooltip"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController, Filler)

type Windows = {
  name: string
  version: number
  url: string
  year: number
}

const dataset: Windows[] = [
  {
    name: "Windows 95",
    version: 95,
    url: "https://en.wikipedia.org/wiki/Windows_95",
    year: 95,
  },
  {
    name: "Windows 98",
    version: 98,
    url: "https://en.wikipedia.org/wiki/Windows_98",
    year: 98,
  },
  {
    name: "Windows 2000",
    version: 2000,
    url: "https://en.wikipedia.org/wiki/Windows_2000",
    year: 2000,
  },
  {
    name: "Windows 7",
    version: 7,
    url: "https://en.wikipedia.org/wiki/Windows_7",
    year: 2009,
  },
  {
    name: "Windows 8",
    version: 8,
    url: "https://en.wikipedia.org/wiki/Windows_8",
    year: 2012,
  },
  {
    name: "Windows 10",
    version: 10,
    url: "https://en.wikipedia.org/wiki/Windows_10",
    year: 2015,
  },
  {
    name: "Windows 11",
    version: 11,
    url: "https://en.wikipedia.org/wiki/Windows_11",
    year: 2021,
  },
]

export function ChartSample(): ReactElement {
  const { rangeSliderProps, isSelected } = useRangeFilter({
    items: dataset,
    getValue: (windows) => windows.year,
    getLabel: (windows, index) => `[${index}] ${windows.name}`,
  })
  const filteredDataset = dataset.filter(isSelected)
  const { renderTooltip, onClick } = useTooltip({
    dataset: filteredDataset,
    TooltipContent: ({ item: windows }) => (
      <div>
        <span>{windows.name}</span>
      </div>
    )
  })
  const props = getChartProps({
    datasetLabel: "Windows",
    color: "#4488cc",
    xTitle: "version name",
    yTitle: "version number",
    dataset: filteredDataset,
    getValue: (windows) => ({
      x: windows.name,
      y: windows.version,
    }),
    labels: ["Invalid name"].concat(filteredDataset.map((windows) => windows.name)),
    labelToTick: (label) => `[${label}]`,
    onClick,
  })
  return (
    <div style={{ maxWidth: "800px" }}>
      <RangeSlider {...rangeSliderProps} />
      <Chart {...props} />
      {renderTooltip()}
    </div>
  )
}
