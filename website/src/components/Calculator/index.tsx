import React, { useEffect, useRef, useState } from "react"
import TeX from "@matejmazur/react-katex"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { fromString, StyledMessage, StyledString } from "./styled"
import { CalculatorForm } from "./CalculatorForm"

function styledStringtoJSXElement(styledString: StyledString, key: number): JSX.Element {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "normal":
      return <span key={key}>{styledString.content}</span>
    case "math":
      return <TeX key={key} math={styledString.content} settings={{ output: "html", macros: macros }} />
    // ↑{ output: "html" } is necessary to avoid strange behavior in 'overflow: scroll' (see memo.md for details)
  }
}

function styledMessagetoJSXElement(styledMessage: StyledMessage, key: number = 0): JSX.Element {
  let style: string
  switch (styledMessage.messageType) {
    case "success":
      style = styles.messageSuccess
      break
    case "error":
      style = styles.messageError
      break
  }
  return (
    <div key={key} className={style}>
      {styledMessage.strings.map((styledString, index) => styledStringtoJSXElement(styledString, index))}
    </div>
  )
}

export function Calculator(): JSX.Element {
  const initialMessage = fromString("success", "Computation results will be shown here")
  const [messages, setMessages] = useState<StyledMessage[]>([initialMessage])
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
    if (div !== null) {
      setTimeout(() => {
        div.scrollTo({ top: div.scrollHeight, behavior: "smooth" })
      })
    }
  }
  useEffect(() => { scrollToBottom() }, [messages])
  return (
    <div className={styles.calculator}>
      <CalculatorForm printMessages={addMessages} />
      <div className={styles.calculatorResults} ref={scrollRef}>
        {messages.map((message, index) => styledMessagetoJSXElement(message, index))}
      </div>
    </div>
  )
}
