import { isDevelopmentMode } from "@site/src/utils/isDevelopmentMode"
import type { Props } from "@theme/NavbarItem"
import NavbarItem from "@theme-original/NavbarItem"
import React from "react"

export interface NavbarItemOnlyDevModeProps {
  label: string
  href?: string
  to?: string
  navbarItemProps: Props
}

export function NavbarItemOnlyDevMode({
  navbarItemProps,
  ...rest
}: NavbarItemOnlyDevModeProps): React.JSX.Element {
  // On mobile devices, props contains { mobile: true, onClick: (some function) }.
  // We need to pass these to NavbarItem to render the item correctly.
  return (
    isDevelopmentMode()
      ? (
        <NavbarItem
          {...navbarItemProps}
          {...rest}
        />
      )
      : <React.Fragment/>
  )
}
