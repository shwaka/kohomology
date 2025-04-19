import { useLocation } from "@docusaurus/router"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import { isDevelopmentMode } from "@site/src/utils/isDevelopmentMode"
import NavbarItem from "@theme-original/NavbarItem"
import type { Props } from "@theme/NavbarItem"
import React from "react"

function GoToPublishedPageOnDevMode(props: Props): React.JSX.Element {
  // On mobile devices, props contains { mobile: true, onClick: (some function) }.
  // We need to pass these to NavbarItem to render the item correctly.
  const context = useDocusaurusContext()
  const location = useLocation()
  const url = context.siteConfig.url
  const pathname = location.pathname
  const search = location.search
  const hash = location.hash
  return (
    <NavbarItem
      {...props}
      href={`${url}${pathname}${search}${hash}`}
      label="Published page"
    />
  )
}

export function GoToPublishedPage(props: Props): React.JSX.Element {
  return (
    isDevelopmentMode()
      ? <GoToPublishedPageOnDevMode {...props}/>
      : <React.Fragment></React.Fragment>
  )
}
