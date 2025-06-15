import React from "react"

import { MessageBoxForWorker } from "@calculator/MessageBoxForWorker"
import { fromString } from "@calculator/styled/message"
import BrowserOnly from "@docusaurus/BrowserOnly"
import { Box, ThemeProvider } from "@mui/material"
import "katex/dist/katex.min.css"

import { CalculatorForm } from "./CalculatorForm"
import { useCustomTheme } from "./useCustomTheme"
import { kohomologyWorkerContext } from "./worker/kohomologyWorkerContext"

function CalculatorImpl(): React.JSX.Element {
  const theme = useCustomTheme()

  return (
    <ThemeProvider theme={theme}>
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          justifyContent: "center",
          paddingTop: "10px",
          paddingBottom: "10px",
        }}
      >
        <kohomologyWorkerContext.Provider
          defaultState={{
            // This is a dummy and shouldn't be used.
            json: "[]",
            idealJson: "[]",
            dgaInfo: [],
            idealInfo: fromString("success", ""),
            workerInfo: { status: "idle"},
          }}
        >
          <CalculatorForm/>
          <MessageBoxForWorker context={kohomologyWorkerContext}/>
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}

export function Calculator(): React.JSX.Element {
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <CalculatorImpl/>}
    </BrowserOnly>
  )
}
