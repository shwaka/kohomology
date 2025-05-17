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
  const result: MessageOptions = [
    {
      text: messageOptionsKt.plainString,
      label: "Copy this line",
    },
  ]
  if ((messageOptionsKt.dgaJson !== null) && (messageOptionsKt.dgaJson !== undefined)) {
    result.push({
      text: messageOptionsKt.dgaJson,
      label: "Copy this DGA as JSON",
    })
  }
  return result
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
