import React from "react"
import TeX from "@matejmazur/react-katex"
import { StyledStringKt, StyledMessageKt } from "kohomology-js"

const styleTypes = ["normal", "math"] as const
type StyleType = typeof styleTypes[number]

export class StyledString {
  readonly styleType: StyleType
  readonly content: string

  constructor(styleType: StyleType, content: string) {
    this.styleType = styleType
    this.content = content
  }

  toJSXElement(key: number): JSX.Element {
    switch (this.styleType) {
      case "normal":
        return <span key={key}>{this.content}</span>
      case "math":
        return <TeX key={key} math={this.content}/>
    }
  }
}

const messageTypes = ["success", "error"] as const
type MessageType = typeof messageTypes[number]

export class StyledMessage {
  readonly messageType: MessageType
  readonly strings: StyledString[]

  constructor(messageType: MessageType, strings: StyledString[]) {
    this.messageType = messageType
    this.strings = strings
  }

  toJSXElement(key: number): JSX.Element {
    switch (this.messageType) {
      case "success":
      case "error":
        return (
          <div key={key}>
            { this.strings.map((styledString, index) => styledString.toJSXElement(index)) }
          </div>)
    }
  }
}

export function toStyledString(styledStringKt: StyledStringKt): StyledString {
  const styleType: string = styledStringKt.styleType
  if (!(styleTypes as readonly string[]).includes(styleType)) {
    throw new Error(`Invalid styleType: ${styleType}`)
  }
  return new StyledString(styleType as StyleType, styledStringKt.content)
}

export function toStyledMessage(styledMessageKt: StyledMessageKt): StyledMessage {
  const messageType: string = styledMessageKt.messageType
  if (!(messageTypes as readonly string[]).includes(messageType)) {
    throw new Error(`Invalid messageType: ${messageType}`)
  }
  return new StyledMessage(messageType as MessageType, styledMessageKt.strings.map(toStyledString))
}
