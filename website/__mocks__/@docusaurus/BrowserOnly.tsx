import React, { Fragment } from "react"

interface Props {
  children: () => JSX.Element
  fallback?: JSX.Element
}

export default function BrowserOnly(props: Props): JSX.Element {
  return (<Fragment>{props.children()}</Fragment>)
}
