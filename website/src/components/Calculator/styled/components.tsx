import TeX from "@matejmazur/react-katex"
import React from "react"
import "katex/dist/katex.min.css"
import { formatStyledMessage, StyledMessage, StyledString } from "./message"
import styles from "./styles.module.scss"

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

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): JSX.Element {
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
    <div className={style} data-styled-message={formatStyledMessage(styledMessage)}>
      {styledMessage.strings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index}/>
      ))}
    </div>
  )
}
