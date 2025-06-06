import { isDevelopmentMode } from "@site/src/utils/isDevelopmentMode"
import type { Props } from "@theme/NavbarItem"
import NavbarItem from "@theme-original/NavbarItem"
import React from "react"

export interface NavbarItemOnlyDevModeProps {
  href: string
  label: string
  navbarItemProps: Props
}

export function NavbarItemOnlyDevMode({
  href, label, navbarItemProps,
}: NavbarItemOnlyDevModeProps): React.JSX.Element {
  // On mobile devices, props contains { mobile: true, onClick: (some function) }.
  // We need to pass these to NavbarItem to render the item correctly.
  return (
    isDevelopmentMode()
      ? <NavbarItem {...navbarItemProps} href={href} label={label}/>
      : <React.Fragment/>
  )
}
