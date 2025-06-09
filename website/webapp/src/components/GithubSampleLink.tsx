import React from "react"

import { githubMainUrl } from "@data"

export function GithubSampleLink(props: { children: string }): React.JSX.Element {
  const url = `${githubMainUrl}/website/sample/src/main/kotlin/`
  return (
    <a href={url} target="_blank" rel="noreferrer">
      {props.children}
    </a>
  )
}
