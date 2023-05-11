import { Box, ThemeProvider } from "@mui/material"
import React, { useCallback, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import { useJsonFromURLQuery } from "../CalculatorForm/urlQuery"
import { kohomologyWorkerContext } from "../kohomologyWorkerContext"
import { MessageBox } from "./MessageBox"
import { fromString, StyledMessage } from "../styled/message"
import { useWorker } from "../WorkerContext"
import { WorkerOutput } from "../worker/workerInterface"

export function MessageBoxForWorker(): JSX.Element {
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
