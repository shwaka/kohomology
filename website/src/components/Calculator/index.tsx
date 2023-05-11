import { Box, ThemeProvider } from "@mui/material"
import React, { useCallback, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { sphere } from "./DGAEditorDialog/examples"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { MessageBox } from "./MessageBox"
import { fromString, StyledMessage } from "./styled/message"
import { useCustomTheme } from "./useCustomTheme"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"
import { useWorker } from "./WorkerContext"
import { WorkerOutput } from "./worker/workerInterface"

function MessageBoxForWorker(): JSX.Element {
  const queryResult = useJsonFromURLQuery()
  const { addListener, addRestartListener } = useWorker(kohomologyWorkerContext)
  const initialMessageArray = [fromString("success", "Computation results will be shown here")]
  if (queryResult.type === "parseError") {
    initialMessageArray.push(
      fromString("error", queryResult.errorMessage)
    )
  }
  const [messages, setMessages] = useState<StyledMessage[]>(initialMessageArray)

  const addMessages = useCallback((addedMessages: StyledMessage | StyledMessage[]): void => {
    if (addedMessages instanceof Array) {
      setMessages((prevMessages) => prevMessages.concat(addedMessages))
    } else {
      setMessages((prevMessages) => prevMessages.concat([addedMessages]))
    }
  }, [setMessages])

  const onmessage = useCallback((output: WorkerOutput): void => {
    if (output.command === "printMessages") {
      addMessages(output.messages)
    }
  }, [addMessages])

  useEffect(() => {
    addListener("MessageBoxForWorker", onmessage)
  }, [addListener, onmessage])

  useEffect(() => {
    addRestartListener("MessageBoxForWorker", () => {
      addMessages(fromString("success", "The background process is restarted."))
    })
  }, [addRestartListener, addMessages])

  return (
    <MessageBox messages={messages}/>
  )
}

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
