import { Box, ThemeProvider } from "@mui/material"
import React, { useCallback, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { sphere } from "./DGAEditorDialog/examples"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { MessageBoxForWorker } from "./MessageBoxForWorker"
import { fromString, StyledMessage } from "./styled/message"
import { useCustomTheme } from "./useCustomTheme"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"
import { useWorker } from "./WorkerContext"
import { WorkerOutput } from "./worker/workerInterface"

export function Calculator(): JSX.Element {
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
        >
          <CalculatorForm/>
          <MessageBoxForWorker/>
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}
