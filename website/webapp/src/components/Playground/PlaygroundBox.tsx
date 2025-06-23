import { useCallback, ReactElement, ReactNode } from "react"

import { useRadio } from "./UseRadio"

interface UsePlaygroundBoxReturnValue {
  props: PlaygroundBoxProps
  renderControl: () => ReactElement
}

export function usePlaygroundBox(): UsePlaygroundBoxReturnValue {
  const { value: width, renderRadio: renderWidthRadio } = useRadio({
    name: "width",
    candidates: ["30%", "50%", "90%", "100%"],
    defaultValue: "100%",
  })
  const { value: padding, renderRadio: renderPaddingRadio } = useRadio({
    name: "padding",
    candidates: ["0px", "1px", "3px", "5px", "10px"],
    defaultValue: "5px",
  })
  const props: PlaygroundBoxProps = {
    width, padding,
  }
  const renderControl = useCallback(() => (
    <div>
      <ul>
        <li>{renderWidthRadio()}</li>
        <li>{renderPaddingRadio()}</li>
      </ul>
    </div>
  ), [renderWidthRadio, renderPaddingRadio])
  return { props, renderControl }
}

interface PlaygroundBoxProps {
  width: string
  padding: string
}

type PlaygroundBoxPropsWithChildren = PlaygroundBoxProps & {
  children: ReactNode
}

export function PlaygroundBox({
  children, width, padding,
}: PlaygroundBoxPropsWithChildren): ReactElement {
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
          padding,
        }}
      >
        {children}
      </div>
    </div>
  )
}
