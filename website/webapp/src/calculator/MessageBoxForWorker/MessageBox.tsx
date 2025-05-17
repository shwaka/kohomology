import { Paper } from "@mui/material"
import React from "react"
import { ShowStyledMessage } from "../Calculator/styled/ShowStyledMessage"
import { StyledMessage } from "../Calculator/styled/message"
import { useScrollToBottom } from "../useScrollToBottom"

interface MessageBoxProps {
  messages: StyledMessage[]
}

export function MessageBox({ messages }: MessageBoxProps): React.JSX.Element {
  const scrollRef = useScrollToBottom([messages])

  return (
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
  )
}
