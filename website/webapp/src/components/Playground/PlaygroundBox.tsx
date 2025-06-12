import React, { useCallback } from "react"

import { useRadio } from "./UseRadio"

interface UsePlaygroundBoxReturnValue {
  props: PlaygroundBoxProps
  renderControl: () => React.JSX.Element
}

export function usePlaygroundBox(): UsePlaygroundBoxReturnValue {
  const { value: width, renderRadio: renderWidthRadio } = useRadio({
    name: "width",
    candidates: ["30%", "50%", "90%", "100%"],
    defaultValue: "100%",
  })
  const props: PlaygroundBoxProps = {
    width
  }
  const renderControl = useCallback(() => (
    <div>
      {renderWidthRadio()}
    </div>
  ), [renderWidthRadio])
  return { props, renderControl }
}

interface PlaygroundBoxProps {
  width: string
}

type PlaygroundBoxPropsWithChildren = PlaygroundBoxProps & {
  children: React.ReactNode
}

export function PlaygroundBox({
  children, width
}: PlaygroundBoxPropsWithChildren): React.JSX.Element {
  return (
    <div
      style={{
        padding: "10px",
        boxSizing: "border-box",
      }}
    >
      <div
        style={{
          width,
          border: "1px solid gray",
          boxSizing: "border-box",
        }}
      >
        {children}
      </div>
    </div>
  )
}
