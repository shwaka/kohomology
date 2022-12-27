import { useLocation } from "@docusaurus/router"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import NavbarItem from "@theme-original/NavbarItem"
import type { Props } from "@theme/NavbarItem"
import React from "react"

function isDevelopmentMode(): boolean {
  // https://docusaurus.io/docs/advanced/ssg#node-env
  return process.env.NODE_ENV === "development"
}

function GoToPublishedPageOnDevMode(props: Props): JSX.Element {
  // On mobile devices, props contains { mobile: true, onClick: (some function) }.
  // We need to pass these to NavbarItem to render the item correctly.
  const context = useDocusaurusContext()
  const location = useLocation()
  const url = context.siteConfig.url
  const pathname = location.pathname
  return (
    <NavbarItem
      {...props}
      href={`${url}${pathname}`}
      label="Published page"
    />
  )
}

export function GoToPublishedPage(props: Props): JSX.Element {
  return (
    isDevelopmentMode()
      ? <GoToPublishedPageOnDevMode {...props}/>
      : <></>
  )
}
