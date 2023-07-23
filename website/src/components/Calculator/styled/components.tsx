import TeX from "@matejmazur/react-katex"
import React from "react"
import "katex/dist/katex.min.css"
import { formatStyledMessage, MessageType, StyledMessage, StyledString } from "./message"
import styles from "./styles.module.scss"
import { IconButton } from "@mui/material"
import MoreHorizIcon from "@mui/icons-material/MoreHoriz"

function Text({ content }: { content: string } ): JSX.Element {
  const lines = content.split("\n")
  return (
    <span>
      {lines.map((line, lineNumber) => (
        // If content ends with the newline,
        // the last element of lines is the empty string "".
        // Hence there is no need to write
        //   lineNumber < lines.length - 1 || content.endsWith("\n")
        (lineNumber < lines.length - 1) ? (
          <React.Fragment key={lineNumber}>
            {line}<br/>
          </React.Fragment>
        ): (
          <React.Fragment key={lineNumber}>
            {line}
          </React.Fragment>
        )
      ))}
    </span>
  )
}

function ShowStyledString({ styledString }: { styledString: StyledString }): JSX.Element {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "text":
      return <Text content={styledString.content}/>
    case "math":
      return <TeX math={styledString.content} settings={{ output: "html", macros: macros }} />
      // ↑{ output: "html" } is necessary to avoid strange behavior in 'overflow: scroll' (see memo.md for details)
  }
}

function getStyle(messageType: MessageType): string {
  switch (messageType) {
    case "success":
      return styles.messageSuccess
    case "error":
      return styles.messageError
  }
}

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): JSX.Element {
  const style: string = getStyle(styledMessage.messageType)
  return (
    <div
      className={style}
      data-styled-message={formatStyledMessage(styledMessage)}
      style={{ position: "relative" }}
    >
      {styledMessage.strings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index}/>
      ))}
      <IconButton
        size="small"
        sx={{
          paddingTop: 0, paddingBottom: 0,
          position: "absolute", bottom: "4px", right: 0,
        }}
      >
        <MoreHorizIcon fontSize="small"/>
      </IconButton>
    </div>
  )
}
