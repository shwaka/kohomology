import { TextField } from "@mui/material"
import React, { useState } from "react"

export interface NumberFieldProps {
  label: string
  value: number
  setValue: (value: number) => void
  width: number
}

export function NumberField({ label, value, setValue, width }: NumberFieldProps): JSX.Element {
  return (
    <TextField
      label={label} value={value} type="number"
      onChange={(e) => setValue(parseInt(e.target.value))}
      sx={{ width: width }} size="small"
    />
  )
}

export interface UseNumberFieldArgs {
  label: string
  defaultValue: number
  width?: number
}

export function useNumberField({ label, defaultValue, width }: UseNumberFieldArgs): [number, NumberFieldProps] {
  const [value, setValue] = useState<number>(defaultValue)
  const props: NumberFieldProps = {
    label, value, setValue,
    width: width !== undefined ? width : 70
  }
  return [value, props]
}
