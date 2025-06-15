import React, { CSSProperties } from "react"

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
  const { optionsButtonProps, open } = useOptionsButton(divClass, styledMessage.options)
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
      {styledMessage.strings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index}/>
      ))}
      <OptionsButton {...optionsButtonProps}/>
    </div>
  )
}
