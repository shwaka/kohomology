import { CSSProperties, ReactElement } from "react"

import { useTruncatedList } from "@calculator/useTruncatedList"
import { ColorMode, useColorMode } from "@docusaurus/theme-common"

import { MessageType, StyledMessage } from "./message"
import { OptionsButton, useOptionsButton } from "./OptionsButton"
import { ShowStyledString } from "./ShowStyledString"

function getBackgroundColorOnMenuOpen(colorMode: ColorMode): string {
  switch (colorMode) {
    case "light":
      return "aquamarine"
    case "dark":
      return "darkslateblue"
  }
}

function getBackgroundColorOnError(colorMode: ColorMode): string {
  switch (colorMode) {
    case "light":
      return "peachpuff"
    case "dark":
      return "darkred"
  }
}

function useStyleForBackground(messageType: MessageType, open: boolean): CSSProperties {
  const { colorMode } = useColorMode()
  if (open) {
    return {
      background: getBackgroundColorOnMenuOpen(colorMode)
    }
  }
  switch (messageType) {
    case "success":
      return {}
    case "error":
      return { background: getBackgroundColorOnError(colorMode) }
  }
}

function getWeightOfLatexCode(code: string): number {
  const replaced = code
    .replace(/\\[a-zA-Z]+/g, "") // Remove control sequence
    .replace(/[{}^_ ]/g, "") // Remove {, }, ^, _
  return replaced.length
}

export function ShowStyledMessage({ styledMessage }: { styledMessage: StyledMessage }): ReactElement {
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
  const styleForBackground: CSSProperties = useStyleForBackground(styledMessage.messageType, open)
  return (
    <div
      className={divClass}
      data-styled-message={styledMessage.plainString}
      data-testid="show-styled-message"
      style={{
        ...styleForBackground,
        borderBottom: "1px solid lightGray",
        position: "relative", // to be used in IconButton in OptionsButton
      }}
    >
      {visibleStrings.map((styledString, index) => (
        <ShowStyledString styledString={styledString} key={index} />
      ))}
      {isTruncated && (<span>...</span>)}
      <OptionsButton {...optionsButtonProps} />
    </div>
  )
}
