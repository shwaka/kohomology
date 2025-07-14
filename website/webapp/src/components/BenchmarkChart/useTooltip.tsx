import { Fragment, ReactElement, ReactNode, useCallback, useState } from "react"

import { Tooltip } from "@mui/material"
import { ActiveElement, ChartOptions } from "chart.js"

import { useArrayChanged } from "./useArrayChanged"

export type OnChartClick = NonNullable<ChartOptions<"line">["onClick"]>

type LabelData = {
  borderColor: string
  backgroundColor: string
}

type TooltipData = {
  x: number
  y: number
  index: number
  labelData: LabelData | null
}

export type TooltipContentProps<T, D> = {
  item: T
  renderBox: () => ReactNode
  globalData: D
}

type UseTooltipReturnValue = {
  renderTooltip: () => ReactNode
  onClick: OnChartClick
}

export function useTooltip<T, D>(
  { dataset, TooltipContent, globalData }: {
    dataset: T[]
    TooltipContent: (props: TooltipContentProps<T, D>) => ReactElement
    globalData: D
  }
): UseTooltipReturnValue {
  const [tooltipData, setTooltipData] = useState<TooltipData | null>(null)
  useArrayChanged(dataset, () => { setTooltipData(null) })
  const onClick: OnChartClick = useCallback((_event, elements, chart) => {
    const element: ActiveElement | undefined = elements[0]
    if (element === undefined) {
      setTooltipData(null)
      return
    }

    const { index } = element
    const point = chart.getDatasetMeta(0).data[index]
    const options = chart.getDatasetMeta(0)?.dataset?.options
    const labelData: LabelData | null = getLabelData(options)

    const x = chart.canvas.offsetLeft + point.x
    const y = chart.canvas.offsetTop + point.y
    setTooltipData({ x, y, index, labelData })
  }, [setTooltipData])
  const renderBox: () => ReactNode = useCallback(() => (
    <ColorBox labelData={tooltipData?.labelData} />
  ), [tooltipData])
  const renderTooltip: () => ReactNode = useCallback(() => {
    if (tooltipData === null) {
      return null
    }
    const item: T | undefined = dataset[tooltipData.index]
    if (item === undefined) {
      return null
    }
    return (
      <TooltipImpl x={tooltipData.x} y={tooltipData.y}>
        <TooltipContent
          item={item}
          renderBox={renderBox}
          globalData={globalData}
        />
      </TooltipImpl>
    )
  }, [tooltipData, dataset, TooltipContent, renderBox, globalData])
  return { onClick, renderTooltip }
}

function getLabelData(options: object | undefined): LabelData | null {
  if (options === undefined) {
    return null
  }
  if (("borderColor" in options) && ("backgroundColor" in options)) {
    const { borderColor, backgroundColor } = options
    if ((typeof borderColor === "string") && (typeof backgroundColor === "string")) {
      return { borderColor, backgroundColor }
    }
  }
  return null
}

function TooltipImpl(
  { children, x, y }: {
    children: ReactNode
    x: number
    y: number
  }
): ReactElement {
  return (
    <Tooltip
      title={children}
      arrow open
      slotProps={{
        tooltip: {
          sx: {
            // make tooltip clickable (overriding pointerEvents for popper)
            pointerEvents: "auto",
            maxWidth: "500px",
          },
        },
        popper: {
          sx: {
            // make popper transparent for mouse click
            pointerEvents: "none",
          },
          modifiers: [
            {
              name: "offset",
              options: {
                offset: [0, -5],
              }
            }
          ]
        }
      }}
    >
      <span
        style={{
          position: "absolute",
          top: y,
          left: x,
        }}
      />
    </Tooltip>
  )
}

function ColorBox(
  { labelData }: { labelData: LabelData | undefined | null }
): ReactElement {
  if ((labelData === undefined) || (labelData === null)) {
    return <Fragment />
  }
  const { borderColor, backgroundColor } = labelData
  // based on https://www.chartjs.org/docs/latest/samples/tooltip/html.html
  return (
    <span
      style={{
        borderWidth: "2px",
        borderStyle: "solid",
        borderColor: borderColor.toString(),
        background: backgroundColor.toString(),
        height: "10px",
        width: "10px",
        display: "inline-block",
      }}
    />
  )
}
