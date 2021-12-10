import React from "react"
import TeX from "@matejmazur/react-katex"
import styles from "./styles.module.css"
import { StyledStringKt, StyledMessageKt } from "kohomology-js"

const stringTypes = ["normal", "math"] as const
type StringType = typeof stringTypes[number]

export class StyledString {
  readonly stringType: StringType
  readonly content: string

  constructor(stringType: StringType, content: string) {
    this.stringType = stringType
    this.content = content
  }

  toJSXElement(key: number): JSX.Element {
    switch (this.stringType) {
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
    let style: string
    switch (this.messageType) {
      case "success":
        style = styles.messageSuccess
        break
      case "error":
        style = styles.messageError
        break
    }
    return (
      <div key={key} className={style}>
        {this.strings.map((styledString, index) => styledString.toJSXElement(index))}
      </div>
    )

  }
}

export function toStyledString(styledStringKt: StyledStringKt): StyledString {
  const stringType: string = styledStringKt.stringType
  if (!(stringTypes as readonly string[]).includes(stringType)) {
    throw new Error(`Invalid stringType: ${stringType}`)
  }
  return new StyledString(stringType as StringType, styledStringKt.content)
}

export function toStyledMessage(styledMessageKt: StyledMessageKt): StyledMessage {
  const messageType: string = styledMessageKt.messageType
  if (!(messageTypes as readonly string[]).includes(messageType)) {
    throw new Error(`Invalid messageType: ${messageType}`)
  }
  return new StyledMessage(messageType as MessageType, styledMessageKt.strings.map(toStyledString))
}
