import React, { useRef, useState } from "react"

type DivMouseEvent = React.MouseEvent<HTMLDivElement, MouseEvent>

export default function ResizableDiv(): React.JSX.Element {
  const [width, setWidth] = useState(300)
  const [height, setHeight] = useState(200)
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
        border: "1px solid black",
        position: "relative",
      }}
    >
      <div style={{ padding: "10px" }}>Resizable Content</div>
      <div
        onMouseDown={handleMouseDown}
        style={{
          width: "10px",
          height: "10px",
          position: "absolute",
          right: 0,
          bottom: 0,
          cursor: "se-resize",
          backgroundColor: "gray",
        }}
      />
    </div>
  )
}
