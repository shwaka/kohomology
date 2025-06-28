import { Fragment, ReactElement } from "react"

import TeX from "@matejmazur/react-katex"

import "katex/dist/katex.min.css"
import { StyledString } from "./message"

function Text({ content }: { content: string } ): ReactElement {
  const lines = content.split("\n")
  return (
    <span>
      {lines.map((line, lineNumber) => (
        // If content ends with the newline,
        // the last element of lines is the empty string "".
        // Hence there is no need to write
        //   lineNumber < lines.length - 1 || content.endsWith("\n")
        (lineNumber < lines.length - 1) ? (
          <Fragment key={lineNumber}>
            {line}<br />
          </Fragment>
        ): (
          <Fragment key={lineNumber}>
            {line}
          </Fragment>
        )
      ))}
    </span>
  )
}

export function ShowStyledString({ styledString }: { styledString: StyledString }): ReactElement {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "text":
      return <Text content={styledString.content} />
    case "math":
      return <TeX math={styledString.content} settings={{ output: "html", macros: macros }} />
      // ↑{ output: "html" } is necessary to avoid strange behavior in 'overflow: scroll' (see memo.md for details)
  }
}
