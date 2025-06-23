import { Fragment, useState, ReactElement, SyntheticEvent } from "react"

import { Alert, Snackbar } from "@mui/material"

// There is no way to re-open Snackbar.
export function ShowError({ messages }: { messages: string[] }): ReactElement {
  const [open, setOpen] = useState(true)

  if (messages.length === 0) {
    return <Fragment/>
  }

  const handleClose = (_event: SyntheticEvent | Event, reason?: string): void => {
    if (reason === "clickaway") {
      return
    }

    setOpen(false)
  }

  return (
    <Snackbar
      open={open}
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
      onClose={handleClose}
    >
      <Alert
        severity="error"
        onClose={handleClose}
        variant="filled"
        elevation={6}
      >
        {messages.map((message, index) => (
          <div
            key={index}
            style={{ whiteSpace: "pre-wrap" }}
          >
            {message}
          </div>
        ))}
      </Alert>
    </Snackbar>
  )
}
