import { Button, CircularProgress } from "@mui/material"
import React, { ComponentProps } from "react"

type ButtonWithProgressProps = ComponentProps<typeof Button> & {
  computing: boolean
  progress: number
}

export function ButtonWithProgress({ computing, progress, ...buttonProps }: ButtonWithProgressProps): JSX.Element {
  const disabled: boolean = buttonProps.disabled ?? false
  const progressPercent: number = Math.floor(progress * 100)
  return (
    <Button
      {...buttonProps}
      disabled={disabled || computing}
    >
      {computing ? `Computing (${progressPercent}%)` : "Compute"}
      <CircularProgress variant="determinate" value={progressPercent} size={20}/>
    </Button>
  )
}
