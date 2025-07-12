import { ReactElement, ReactNode, useCallback, useState } from "react"

import { Tooltip } from "@mui/material"
import { ActiveElement, ChartOptions } from "chart.js"

import { useArrayChanged } from "./useArrayChanged"

export type OnChartClick = NonNullable<ChartOptions<"line">["onClick"]>

type TooltipData = {
  x: number
  y: number
  index: number
}

type UseTooltipReturnValue = {
  renderTooltip: () => ReactNode
  onClick: OnChartClick
}

export function useTooltip<T>(
  { dataset, TooltipContent }: {
    dataset: T[]
    TooltipContent: (props: { item: T }) => ReactElement
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

    const x = chart.canvas.offsetLeft + point.x
    const y = chart.canvas.offsetTop + point.y
    setTooltipData({ x, y, index })
  }, [setTooltipData])
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
        <TooltipContent item={item} />
      </TooltipImpl>
    )
  }, [tooltipData, dataset, TooltipContent])
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
