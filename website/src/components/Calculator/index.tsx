import { Box, ThemeProvider } from "@mui/material"
import React, { useCallback, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { QueryResult, useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { sphere } from "./DGAEditorDialog/examples"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { MessageBox } from "./MessageBox"
import { fromString, StyledMessage } from "./styled/message"
import { useCustomTheme } from "./useCustomTheme"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"
import { useWorker } from "./WorkerContext"
import { WorkerOutput } from "./worker/workerInterface"

interface MessageBoxWithMessagesProps {
  queryResult: QueryResult
}

function MessageBoxWithMessages({ queryResult }: MessageBoxWithMessagesProps): JSX.Element {
  const { addListener } = useWorker(kohomologyWorkerContext)
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
    addListener("MessageBoxWithMessages", onmessage)
  }, [addListener, onmessage])


  return (
    <MessageBox messages={messages}/>
  )
}

export function Calculator(): JSX.Element {
  const queryResult = useJsonFromURLQuery()
  const defaultDGAJson = (queryResult.type === "success") ? queryResult.json : sphere(2)
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
          <CalculatorForm printMessages={() => {}} defaultDGAJson={defaultDGAJson}/>
          <MessageBoxWithMessages queryResult={queryResult}/>
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}
