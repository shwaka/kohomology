import { ReactElement, ReactNode, useCallback, useState } from "react"

import { Tooltip } from "@mui/material"
import { ActiveElement, ChartOptions, TooltipLabelStyle } from "chart.js"

import { useArrayChanged } from "./useArrayChanged"

export type OnChartClick = NonNullable<ChartOptions<"line">["onClick"]>

type TooltipData = {
  x: number
  y: number
  index: number
  labelStyle: TooltipLabelStyle | undefined
}

export type TooltipContentProps<T> = {
  item: T
  renderBox: () => ReactNode
}

type UseTooltipReturnValue = {
  renderTooltip: () => ReactNode
  onClick: OnChartClick
}

export function useTooltip<T>(
  { dataset, TooltipContent }: {
    dataset: T[]
    TooltipContent: (props: TooltipContentProps<T>) => ReactElement
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
    const labelStyle: TooltipLabelStyle | undefined =
      chart.tooltip?.labelColors?.[0]

    const x = chart.canvas.offsetLeft + point.x
    const y = chart.canvas.offsetTop + point.y
    setTooltipData({ x, y, index, labelStyle })
  }, [setTooltipData])
  const renderBox: () => ReactNode = useCallback(() => {
    if (tooltipData === null) {
      return null
    }
    const { labelStyle } = tooltipData
    if (labelStyle === undefined) {
      return null
    }
    const { borderColor, backgroundColor } = labelStyle
    // based on https://www.chartjs.org/docs/latest/samples/tooltip/html.html
    return (
      <span
        style={{
          borderWidth: "2px",
          height: "10px",
          width: "10px",
          borderColor: borderColor.toString(),
          background: backgroundColor.toString(),
          display: "inline-block",
        }}
      />
    )
  }, [tooltipData])
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
        <TooltipContent item={item} renderBox={renderBox} />
      </TooltipImpl>
    )
  }, [tooltipData, dataset, TooltipContent, renderBox])
  return { onClick, renderTooltip }
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
