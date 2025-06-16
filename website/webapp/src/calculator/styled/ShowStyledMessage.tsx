import React, { CSSProperties } from "react"

import { useTruncatedList } from "@calculator/useTruncatedList"

import { MessageType, StyledMessage } from "./message"
import { OptionsButton, useOptionsButton } from "./OptionsButton"
import { ShowStyledString } from "./ShowStyledString"

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

function getWeightOfLatexCode(code: string): number {
  const replaced = code
    .replace(/\\[a-zA-Z]+/g, "") // Remove control sequence
    .replace(/[{}^_ ]/g, "") // Remove {, }, ^, _
  return replaced.length
}

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): React.JSX.Element {
  const divClass = "show-styled-message"
  const { visibleItems: visibleStrings, commands: { showAll }, isTruncated } = useTruncatedList(
    styledMessage.strings,
    {
      minWeight: 300, step: 100,
      getWeight: (styledString) => getWeightOfLatexCode(styledString.content),
    },
  )
  const { optionsButtonProps, open } = useOptionsButton({
    containerClass: divClass,
    options: styledMessage.options,
    showAll,
  })
  return (
    <div
      className={divClass}
      data-styled-message={styledMessage.plainString}
      data-testid="show-styled-message"
      style={{
        ...getStyleForBackground(styledMessage.messageType, open),
        borderBottom: "1px solid lightGray",
        position: "relative", // to be used in IconButton in OptionsButton
      }}
    >
      {visibleStrings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index}/>
      ))}
      {isTruncated && (<span>...</span>)}
      <OptionsButton {...optionsButtonProps}/>
    </div>
  )
}
