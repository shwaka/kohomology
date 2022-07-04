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
}

export function formatStyledMessage(styledMessage: StyledMessage): string {
  return styledMessage.strings.map((styledString) => styledString.content).join("")
}

export function fromString(messageType: MessageType, str: string): StyledMessage {
  const styledString: StyledString = {
    stringType: "text",
    content: str,
  }
  return {
    messageType: messageType,
    strings: [styledString]
  }
}
