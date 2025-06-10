import React, { useRef, useState } from "react"

type DivMouseEvent = React.MouseEvent<HTMLDivElement, MouseEvent>

interface ResizableBoxProps {
  children: React.ReactNode
  defaultWidth: number
  defaultHeight: number
}

export default function ResizableDiv({
  children, defaultWidth, defaultHeight,
}: ResizableBoxProps): React.JSX.Element {
  const [width, setWidth] = useState(defaultWidth)
  const [height, setHeight] = useState(defaultHeight)
  const isResizing = useRef(false)
  const startX = useRef(0)
  const startY = useRef(0)
  const startWidth = useRef(0)
  const startHeight = useRef(0)

  const handleMouseDown = (e: DivMouseEvent): void => {
    e.preventDefault()
    isResizing.current = true
    startX.current = e.clientX
    startY.current = e.clientY
    startWidth.current = width
    startHeight.current = height
  }

  const handleMouseMove = (e: MouseEvent): void => {
    if (isResizing.current) {
      setWidth(startWidth.current + e.clientX - startX.current)
      setHeight(startHeight.current + e.clientY - startY.current)
    }
  }

  const handleMouseUp = (): void => {
    isResizing.current = false
  }

  React.useEffect(() => {
    window.addEventListener("mousemove", handleMouseMove)
    window.addEventListener("mouseup", handleMouseUp)
    return () => {
      window.removeEventListener("mousemove", handleMouseMove)
      window.removeEventListener("mouseup", handleMouseUp)
    }
  }, [])

  return (
    <div
      style={{
        width,
        height,
        border: "1px solid gray",
        position: "relative",
        margin: "10px",
      }}
    >
      <div style={{ padding: "0px" }}>
        {children}
      </div>
      <div
        onMouseDown={handleMouseDown}
        style={{
          width: "10px",
          height: "10px",
          position: "absolute",
          right: 0,
          bottom: 0,
          cursor: "se-resize",
          backgroundColor: "lightgray",
        }}
      />
    </div>
  )
}
