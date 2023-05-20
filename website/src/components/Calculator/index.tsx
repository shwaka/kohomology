import BrowserOnly from "@docusaurus/BrowserOnly"
import { Box, ThemeProvider } from "@mui/material"
import React from "react"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"
import { CalculatorForm } from "./CalculatorForm"
import { MessageBoxForWorker } from "./MessageBoxForWorker"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { useCustomTheme } from "./useCustomTheme"

function CalculatorImpl(): JSX.Element {
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
            dgaInfo: [],
          }}
        >
          <CalculatorForm/>
          <MessageBoxForWorker/>
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}

export function Calculator(): JSX.Element {
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <CalculatorImpl/>}
    </BrowserOnly>
  )
}
