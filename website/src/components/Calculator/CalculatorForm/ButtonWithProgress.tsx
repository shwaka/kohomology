import { Button, CircularProgress, Stack } from "@mui/material"
import React, { ComponentProps } from "react"

function CircularProgressFromNullable({ value }: { value: number | null }): JSX.Element {
  const size = 20
  if (value === null) {
    return (
      <CircularProgress size={size}/>
    )
  } else {
    return (
      <CircularProgress variant="determinate" value={value} size={size}/>
    )
  }
}

function toPercent(value: number | null): number | null {
  if (value === null) {
    return null
  } else {
    return Math.floor(value * 100)
  }
}

function getLabel(computing: boolean, progressPercent: number | null): string {
  if (!computing) {
    return "Compute"
  }
  if (progressPercent === null) {
    return "Computing"
  }
  return `Computing (${progressPercent}%)`
}

type ButtonWithProgressProps = ComponentProps<typeof Button> & {
  computing: boolean
  progress: number | null
  message?: string
}

export function ButtonWithProgress({ computing, progress, message, ...buttonProps }: ButtonWithProgressProps): JSX.Element {
  const disabled: boolean = buttonProps.disabled ?? false
  const progressPercent: number | null = toPercent(progress)
  return (
    <Stack>
      <Button
        {...buttonProps}
        disabled={disabled || computing}
      >
        {getLabel(computing, progressPercent)}
        {computing && <CircularProgressFromNullable value={progressPercent}/>}
      </Button>
      {message}
    </Stack>
  )
}
