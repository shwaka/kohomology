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

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): React.JSX.Element {
  const divClass = "show-styled-message"
  const { visibleItems: visibleStrings, commands: { showAll }, isTruncated } = useTruncatedList(
    styledMessage.strings,
    { minCount: 10, step: 3 },
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
