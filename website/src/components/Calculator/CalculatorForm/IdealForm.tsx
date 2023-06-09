import { Alert, Button } from "@mui/material"
import React, { useCallback } from "react"
import { StringField, useStringField } from "./StringField"

interface IdealFormProms {
  setIdealJson: (idealJson: string) => void
}

export function IdealForm({ setIdealJson }: IdealFormProms): JSX.Element {
  const [idealJson, idealJsonFieldProps] =
    useStringField({ label: "ideal as json", defaultValue: "[]", width: 200 })

  const handleSubmit = useCallback((event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault()
    setIdealJson(idealJson)
  }, [idealJson, setIdealJson])
  return (
    <div>
      <Alert severity="error">
        This is an experimental feature
        and may contain some bugs!
      </Alert>

      <form onSubmit={handleSubmit}>
        <StringField {...idealJsonFieldProps}/>
        <Button type="submit" sx={{ textTransform: "none" }}>Set ideal</Button>
      </form>
    </div>
  )
}
