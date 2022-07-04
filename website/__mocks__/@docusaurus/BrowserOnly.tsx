import React, { Fragment, ReactNode } from "react"

export default function BrowserOnly(props: { children: ReactNode }): JSX.Element {
  return (<Fragment>{props.children}</Fragment>)
}
