import { StyledStringKt, StyledMessageKt } from "kohomology-js"

const stringTypes = ["normal", "math"] as const
type StringType = typeof stringTypes[number]

export interface StyledString {
  readonly stringType: StringType
  readonly content: string
}

const messageTypes = ["success", "error"] as const
type MessageType = typeof messageTypes[number]

export interface StyledMessage {
  readonly messageType: MessageType
  readonly strings: StyledString[]
}

export function fromString(messageType: MessageType, str: string): StyledMessage {
  const styledString: StyledString = {
    stringType: "normal",
    content: str,
  }
  return {
    messageType: messageType,
    strings: [styledString]
  }
}


export function toStyledString(styledStringKt: StyledStringKt): StyledString {
  const stringType: string = styledStringKt.stringType
  if (!(stringTypes as readonly string[]).includes(stringType)) {
    throw new Error(`Invalid stringType: ${stringType}`)
  }
  return {
    stringType: stringType as StringType,
    content: styledStringKt.content
  }
}

export function toStyledMessage(styledMessageKt: StyledMessageKt): StyledMessage {
  const messageType: string = styledMessageKt.messageType
  if (!(messageTypes as readonly string[]).includes(messageType)) {
    throw new Error(`Invalid messageType: ${messageType}`)
  }
  return {
    messageType: messageType as MessageType,
    strings: styledMessageKt.strings.map(toStyledString)
  }
}
