import Link from "@docusaurus/Link"
import { useLocation } from "@docusaurus/router"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import React from "react"

function isDevelopmentMode(): boolean {
  // https://docusaurus.io/docs/advanced/ssg#node-env
  return process.env.NODE_ENV === "development"
}

function GoToPublishedPageOnDevMode(): JSX.Element {
  const context = useDocusaurusContext()
  const location = useLocation()
  const url = context.siteConfig.url
  const pathname = location.pathname
  return (
    <Link to={`${url}${pathname}`}>
      Published page
    </Link>
  )
}

export function GoToPublishedPage(): JSX.Element {
  return (
    isDevelopmentMode()
      ? <GoToPublishedPageOnDevMode/>
      : <></>
  )
}
