import BrowserOnly from "@docusaurus/BrowserOnly"
import { createTheme, ThemeProvider } from "@mui/material"
import React, { useEffect, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { sphere } from "./DGAEditorDialog/examples"
import { useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { ShowStyledMessage } from "./styled/components"
import { fromString, StyledMessage } from "./styled/message"
import styles from "./styles.module.scss"

const theme = createTheme({
  palette: {
    primary: {
      main: "#7e6ca8", // --ifm-color-primary in src/css/custom.css
    }
  }
})

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
  const scrollRef = useRef<HTMLDivElement>(null)

  function addMessages(addedMessages: StyledMessage | StyledMessage[]): void {
    if (addedMessages instanceof Array) {
      setMessages((prevMessages) => prevMessages.concat(addedMessages))
    } else {
      setMessages((prevMessages) => prevMessages.concat([addedMessages]))
    }
  }

  function scrollToBottom(): void {
    const div: HTMLDivElement | null = scrollRef.current
    if (div !== null && div.scrollTo !== undefined) {
      // div.scrollTo can be undefined in test environment
      setTimeout(() => {
        div.scrollTo({ top: div.scrollHeight, behavior: "smooth" })
      })
    }
  }
  useEffect(() => { scrollToBottom() }, [messages])
  // BrowserOnly is used to avoid SSR (see a comment in CalculatorFrom)
  return (
    <ThemeProvider theme={theme}>
      <div className={styles.calculator} data-testid="Calculator">
        <BrowserOnly fallback={<div>Loading...</div>}>
          {() => <CalculatorForm printMessages={addMessages} defaultDGAJson={defaultDGAJson}/>}
        </BrowserOnly>
        <div className={styles.calculatorResults} ref={scrollRef} data-testid="calculator-results">
          {messages.map((message, index) => <ShowStyledMessage styledMessage={message} key={index}/>)}
        </div>
      </div>
    </ThemeProvider>
  )
}
