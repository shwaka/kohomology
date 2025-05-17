import { TextField } from "@mui/material"
import React, { useState } from "react"

export interface NumberFieldProps {
  label: string
  value: string
  setValue: (value: string) => void
  width: number
  disabled: boolean
}

export function NumberField({ label, value, setValue, width, disabled }: NumberFieldProps): React.JSX.Element {
  return (
    <TextField
      label={label} value={value} type="number"
      onChange={(e) => setValue(e.target.value)}
      sx={{ width: width }} size="small"
      disabled={disabled}
    />
  )
}

export interface UseNumberFieldArgs {
  label: string
  defaultValue: number
  width?: number
  disabled?: boolean
}

export function useNumberField({ label, defaultValue, width, disabled }: UseNumberFieldArgs): [number, NumberFieldProps] {
  const [value, setValue] = useState<string>(defaultValue.toString())
  const props: NumberFieldProps = {
    label, value, setValue,
    width: width !== undefined ? width : 70,
    disabled: disabled !== undefined ? disabled : false,
  }
  return [parseInt(value), props]
}
