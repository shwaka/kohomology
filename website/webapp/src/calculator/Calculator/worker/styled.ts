import { MessageType, messageTypes, StringType, stringTypes, StyledMessage, StyledString } from "@calculator/styled/message"
import { MessageOptions } from "@calculator/styled/options"
import { styled } from "kohomology-js"

export function toStyledString(styledStringKt: styled.StyledStringKt): StyledString {
  const stringType: string = styledStringKt.stringType
  if (!(stringTypes as readonly string[]).includes(stringType)) {
    throw new Error(`Invalid stringType: ${stringType}`)
  }
  return {
    stringType: stringType as StringType,
    content: styledStringKt.content
  }
}

export function toMessageOptions(messageOptionsKt: styled.MessageOptionsKt): MessageOptions {
  return [
    {
      text: messageOptionsKt.plainString,
      label: "Copy this line",
    },
    {
      text: messageOptionsKt.dgaJson ?? null,
      label: "Copy this DGA as JSON",
    },
  ]
}

export function toStyledMessage(styledMessageKt: styled.StyledMessageKt): StyledMessage {
  const messageType: string = styledMessageKt.messageType
  if (!(messageTypes as readonly string[]).includes(messageType)) {
    throw new Error(`Invalid messageType: ${messageType}`)
  }
  return {
    messageType: messageType as MessageType,
    strings: styledMessageKt.strings.map(toStyledString),
    plainString: styledMessageKt.plainString,
    options: toMessageOptions(styledMessageKt.options),
  }
}
