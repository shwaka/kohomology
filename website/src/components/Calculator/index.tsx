import { Box, ThemeProvider } from "@mui/material"
import React, { useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { sphere } from "./DGAEditorDialog/examples"
import { kohomologyWorkerContext } from "./kohomologyWorkerContext"
import { MessageBox } from "./MessageBox"
import { fromString, StyledMessage } from "./styled/message"
import { useCustomTheme } from "./useCustomTheme"
import KohomologyWorker from "worker-loader!./worker/kohomology.worker"

export function Calculator(): JSX.Element {
  const queryResult = useJsonFromURLQuery()
  const defaultDGAJson = (queryResult.type === "success") ? queryResult.json : sphere(2)
  const initialMessageArray = [fromString("success", "Computation results will be shown here")]
  if (queryResult.type === "parseError") {
    initialMessageArray.push(
      fromString("error", queryResult.errorMessage)
    )
  }
  const [messages, setMessages] = useState<StyledMessage[]>(initialMessageArray)
  const theme = useCustomTheme()

  function addMessages(addedMessages: StyledMessage | StyledMessage[]): void {
    if (addedMessages instanceof Array) {
      setMessages((prevMessages) => prevMessages.concat(addedMessages))
    } else {
      setMessages((prevMessages) => prevMessages.concat([addedMessages]))
    }
  }

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
          <CalculatorForm printMessages={addMessages} defaultDGAJson={defaultDGAJson}/>
          <MessageBox messages={messages}/>
        </kohomologyWorkerContext.Provider>
      </Box>
    </ThemeProvider>
  )
}
