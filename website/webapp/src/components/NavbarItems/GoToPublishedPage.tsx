import { ReactElement } from "react"

import { useLocation } from "@docusaurus/router"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import type { Props } from "@theme/NavbarItem"

import { NavbarItemOnlyDevMode } from "./NavbarItemOnlyDevMode"

function usePublishedPageUrl(): string {
  const context = useDocusaurusContext()
  const location = useLocation()
  const url = context.siteConfig.url
  const pathname = location.pathname
  const search = location.search
  const hash = location.hash
  return `${url}${pathname}${search}${hash}`
}

export function GoToPublishedPage(props: Props): ReactElement {
  const href = usePublishedPageUrl()
  return (
    <NavbarItemOnlyDevMode
      navbarItemProps={props}
      label="Published page"
      href={href}
    />
  )
}
