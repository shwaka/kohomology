import { ReactElement } from "react"

import { githubMainUrl } from "@data"

export function GithubSampleLink(props: { children: string }): ReactElement {
  const url = `${githubMainUrl}/website/sample/src/main/kotlin/`
  return (
    <a href={url} target="_blank" rel="noreferrer">
      {props.children}
    </a>
  )
}
