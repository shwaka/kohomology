import { ReactElement } from "react"

import { MessageBoxForWorker } from "@calculator/MessageBoxForWorker"
import { fromString } from "@calculator/styled/message"
import BrowserOnly from "@docusaurus/BrowserOnly"
import { Box, ThemeProvider } from "@mui/material"
import "katex/dist/katex.min.css"

import { CalculatorForm } from "./CalculatorForm"
import { kohomologyWorkerContext } from "./kohomologyWorker/kohomologyWorkerContext"
import { useCustomTheme } from "./useCustomTheme"

function CalculatorImpl(): ReactElement {
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
            workerInfo: { status: "idle" },
          }}
        >
          <CalculatorForm />
          <MessageBoxForWorker context={kohomologyWorkerContext} />
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}

export function Calculator(): ReactElement {
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <CalculatorImpl />}
    </BrowserOnly>
  )
}
