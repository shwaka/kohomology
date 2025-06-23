import { useState, ReactElement } from "react"

import { TextField } from "@mui/material"

export interface StringFieldProps {
  label: string
  value: string
  setValue: (value: string) => void
  width: number
  disabled: boolean
}

export function StringField({ label, value, setValue, width, disabled }: StringFieldProps): ReactElement {
  return (
    <TextField
      label={label} value={value}
      onChange={(e) => setValue(e.target.value)}
      sx={{ width: width }} size="small"
      disabled={disabled}
    />
  )
}

export interface UseStringFieldArgs {
  label: string
  defaultValue: string
  width?: number
  disabled?: boolean
}

export function useStringField({ label, defaultValue, width, disabled }: UseStringFieldArgs): [string, StringFieldProps] {
  const [value, setValue] = useState<string>(defaultValue)
  const props: StringFieldProps = {
    label, value, setValue,
    width: width !== undefined ? width : 70,
    disabled: disabled !== undefined ? disabled : false,
  }
  return [value, props]
}
