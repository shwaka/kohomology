import { TextField } from "@mui/material"
import React, { useState } from "react"

export interface StringFieldProps {
  label: string
  value: string
  setValue: (value: string) => void
  width: number
}

export function StringField({ label, value, setValue, width }: StringFieldProps): JSX.Element {
  return (
    <TextField
      label={label} value={value}
      onChange={(e) => setValue(e.target.value)}
      sx={{ width: width }} size="small"
    />
  )
}

export interface UseStringFieldArgs {
  label: string
  defaultValue: string
  width?: number
}

export function useStringField({ label, defaultValue, width }: UseStringFieldArgs): [string, StringFieldProps] {
  const [value, setValue] = useState<string>(defaultValue)
  const props: StringFieldProps = {
    label, value, setValue,
    width: width !== undefined ? width : 70
  }
  return [value, props]
}
