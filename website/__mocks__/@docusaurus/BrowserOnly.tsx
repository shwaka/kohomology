import React, { Fragment } from "react"

interface Props {
  children: () => React.JSX.Element
  fallback?: React.JSX.Element
}

export default function BrowserOnly(props: Props): React.JSX.Element {
  return (<Fragment>{props.children()}</Fragment>)
}
