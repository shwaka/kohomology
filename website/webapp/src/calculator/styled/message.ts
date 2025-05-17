import { MessageOptions } from "./options"

export const stringTypes = ["text", "math"] as const
export type StringType = typeof stringTypes[number]

export interface StyledString {
  readonly stringType: StringType
  readonly content: string
}

export const messageTypes = ["success", "error"] as const
export type MessageType = typeof messageTypes[number]

export interface StyledMessage {
  readonly messageType: MessageType
  readonly strings: StyledString[]
  readonly plainString: string
  readonly options: MessageOptions
}

export function fromString(messageType: MessageType, str: string): StyledMessage {
  const styledString: StyledString = {
    stringType: "text",
    content: str,
  }
  const options: MessageOptions = [
    {
      text: str,
      label: "Copy this line",
    },
  ]
  return {
    messageType: messageType,
    strings: [styledString],
    plainString: str,
    options: options,
  }
}
