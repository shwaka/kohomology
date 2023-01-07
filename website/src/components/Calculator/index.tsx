import { useColorMode } from "@docusaurus/theme-common"
import { createTheme, Paper, ThemeProvider } from "@mui/material"
import React, { useEffect, useMemo, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import { CalculatorForm } from "./CalculatorForm"
import { useJsonFromURLQuery } from "./CalculatorForm/urlQuery"
import { sphere } from "./DGAEditorDialog/examples"
import { ShowStyledMessage } from "./styled/components"
import { fromString, StyledMessage } from "./styled/message"
import styles from "./styles.module.scss"

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
  const { colorMode } = useColorMode()

  const theme = useMemo(
    () => createTheme({
      palette: {
        mode: colorMode,
        primary: {
          main: "#7e6ca8", // --ifm-color-primary in src/css/custom.css
        }
      }
    }),
    [colorMode]
  )

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
  return (
    <ThemeProvider theme={theme}>
      <div className={styles.calculator} data-testid="Calculator">
        <CalculatorForm printMessages={addMessages} defaultDGAJson={defaultDGAJson}/>
        <Paper
          elevation={0} variant="outlined"
          ref={scrollRef}
          data-testid="calculator-results"
          sx={{
            width: "700px",
            height: "700px",
            overflowY: "scroll",
            padding: "5px",
            "@media screen and (max-width: 700px)": {
              width: "100%",
              height: "50vh",
              margin: "3px",
            }
          }}
        >
          {messages.map((message, index) => <ShowStyledMessage styledMessage={message} key={index}/>)}
        </Paper>
      </div>
    </ThemeProvider>
  )
}
