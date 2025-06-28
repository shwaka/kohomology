import { ReactElement } from "react"

import CodeBlock from "@theme/CodeBlock"
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ChartData, ScatterController } from "chart.js"
import { Chart, ChartProps } from "react-chartjs-2"

import { getBackgroundColor, getBorderColor } from "./colors"
import comparisonData from "./comparison.json"
import { Target, tools, Tool } from "./comparisonKeys"

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend, Title, LineController, ScatterController)

function zip<T1, T2>(array1: T1[], array2: T2[]): [T1, T2][] {
  return array1.map((t1, i) => [t1, array2[i]])
}

type Vector2 = { x: number, y: number }
type FilterCoordinate = (coordinate: Vector2) => boolean

function getData(
  { label, target, tool, borderColor, backgroundColor, filterCoordinate }: {
    label: string
    target: Target
    tool: Tool
    borderColor: string
    backgroundColor: string
    filterCoordinate: FilterCoordinate
  }
): ChartData<"scatter", { x: number, y: number }[], string>["datasets"][number] {
  const dataForLine: { time: number[], degrees: number[] } = comparisonData[tool].benchmark[target]
  return {
    label: label,
    data: zip(dataForLine.degrees, dataForLine.time).map(([x, y]) => ({ x, y })).filter(filterCoordinate),
    showLine: true,
    borderColor: borderColor,
    backgroundColor: backgroundColor,
  }
}

function getDataForArray(
  datasetInfoArray: { label: string, target: Target, tool: Tool }[],
  filterCoordinate: FilterCoordinate | undefined = undefined,
): ChartData<"scatter", Vector2[], string> {
  const filterCoordinateDefined: FilterCoordinate = filterCoordinate ?? ((_) => true)
  const datasets = datasetInfoArray.map(({ label, target, tool }, i) => getData({
    label, target, tool,
    borderColor: getBorderColor(i),
    backgroundColor: getBackgroundColor(i),
    filterCoordinate: filterCoordinateDefined,
  }))
  return { datasets }
}

function getDataForTarget(target: Target): ChartData<"scatter", Vector2[], string> {
  const datasetInfoArray = tools.map((tool) => ({
    label: tool,
    target, tool,
  }))
  return getDataForArray(datasetInfoArray)
}

function getOptions(titleText: string | null = null): ChartProps<"scatter", { x: number, y: number }[], string>["options"] {
  const title = (titleText !== null) ? { display: true, text: titleText } : { display: false }
  return {
    plugins: {
      title
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
}

export function ComparisonChart({ target }: { target: Target }): ReactElement {
  const data = getDataForTarget(target)
  return (
    <Chart
      type="scatter"
      data={data}
      options={getOptions(target)}
    />
  )
}

export function ComparisonChartForDegrees(): ReactElement {
  const data = getDataForArray([
    { label: "IntDegree", target: "FreeLoopSpaceOf2Sphere", tool: "kohomology" },
    { label: "MultiDegree", target: "FreeLoopSpaceOf2SphereWithMultiGrading", tool: "kohomology" },
  ])
  return (
    <Chart
      type="scatter"
      data={data}
      options={getOptions()}
    />
  )
}

export function ShowVersion({ tool }: { tool: Tool }): ReactElement {
  return (
    <CodeBlock
      language="shell-session"
      title={tool}
    >
      {comparisonData[tool].version}
    </CodeBlock>
  )
}
