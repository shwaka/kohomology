import { useCallback, useEffect, useState, ReactElement } from "react"

import "katex/dist/katex.min.css"
import { fromString, StyledMessage } from "../styled/message"
import { useWorker } from "../WorkerContext"
import { MessageBox } from "./MessageBox"
import { WFBase } from "../WorkerContext/expose"
import { WorkerContext } from "../WorkerContext/WorkerContext"

export const printMessagesCommand = "printMessages" as const

export type SendMessage = {
  command: typeof printMessagesCommand
  messages: StyledMessage[]
}

interface MessageBoxForWorkerProps<WI, WO extends SendMessage, WS, WF extends WFBase> {
  context: WorkerContext<WI, WO, WS, WF>
}

export function MessageBoxForWorker<WI, WO extends SendMessage, WS, WF extends WFBase>(
  { context }: MessageBoxForWorkerProps<WI, WO, WS, WF>
): ReactElement {
  const { addListener, addRestartListener } = useWorker(context)
  const initialMessageArray = [fromString("success", "Computation results will be shown here")]
  const [messages, setMessages] = useState<StyledMessage[]>(initialMessageArray)

  const addMessages = useCallback((addedMessages: StyledMessage | StyledMessage[]): void => {
    if (addedMessages instanceof Array) {
      setMessages((prevMessages) => prevMessages.concat(addedMessages))
    } else {
      setMessages((prevMessages) => prevMessages.concat([addedMessages]))
    }
  }, [setMessages])

  const onmessage = useCallback((output: WO): void => {
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
