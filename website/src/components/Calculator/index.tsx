import React, { useEffect, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { fromString, StyledMessage } from "./styled"
import { CalculatorForm, styledMessageToJSXElement } from "./CalculatorForm"

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
        {messages.map((message, index) => styledMessageToJSXElement(message, index))}
      </div>
    </div>
  )
}
