import React, { useCallback, useState } from "react"

interface UsePlaygroundBoxReturnValue {
  props: PlaygroundBoxProps
  renderControl: () => React.JSX.Element
}

const widthCandidates = ["30%", "50%", "90%", "100%"]

export function usePlaygroundBox(): UsePlaygroundBoxReturnValue {
  const [width, setWidth] = useState("100%")
  const props: PlaygroundBoxProps = {
    width
  }
  const renderControl = useCallback(() => (
    <div>
      <div>
        width:
        {widthCandidates.map((_width) => (
          <label key={_width}>
            <input
              type="radio"
              value={_width}
              checked={_width === width}
              onChange={(e) => setWidth(e.target.value)}
            />
            {_width}
          </label>
        ))}
      </div>
    </div>
  ), [width, setWidth])
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
        width,
        border: "1px solid gray",
        boxSizing: "border-box",
      }}
    >
      {children}
    </div>
  )
}
