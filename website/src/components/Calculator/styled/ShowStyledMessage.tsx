import TeX from "@matejmazur/react-katex"
import React, { CSSProperties } from "react"
import "katex/dist/katex.min.css"
import { OptionsButton, useOptionsButton } from "./OptionsButton"
import { MessageType, StyledMessage, StyledString } from "./message"

function Text({ content }: { content: string } ): React.JSX.Element {
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

function ShowStyledString({ styledString }: { styledString: StyledString }): React.JSX.Element {
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

function getStyleForBackground(messageType: MessageType, open: boolean): CSSProperties {
  if (open) {
    return {
      background: "aquamarine"
    }
  }
  switch (messageType) {
    case "success":
      return {}
    case "error":
      return { background: "peachpuff" }
  }
}

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): React.JSX.Element {
  const divClass = "show-styled-message"
  const { optionsButtonProps, open } = useOptionsButton(divClass, styledMessage.options)
  return (
    <div
      className={divClass}
      data-styled-message={styledMessage.plainString}
      style={{
        ...getStyleForBackground(styledMessage.messageType, open),
        borderBottom: "1px solid lightGray",
        position: "relative", // to be used in IconButton in OptionsButton
      }}
    >
      {styledMessage.strings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index}/>
      ))}
      <OptionsButton {...optionsButtonProps}/>
    </div>
  )
}
