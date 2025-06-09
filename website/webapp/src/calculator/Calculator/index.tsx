import React from "react"

import { MessageBoxForWorker } from "@calculator/MessageBoxForWorker"
import { fromString } from "@calculator/styled/message"
import BrowserOnly from "@docusaurus/BrowserOnly"
import { Box, ThemeProvider } from "@mui/material"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"

import { CalculatorForm } from "./CalculatorForm"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { useCustomTheme } from "./useCustomTheme"

function CalculatorImpl(): React.JSX.Element {
  const theme = useCustomTheme()

  const createWorker = (): Worker => new KohomologyWorker()

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
          createWorker={createWorker}
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
