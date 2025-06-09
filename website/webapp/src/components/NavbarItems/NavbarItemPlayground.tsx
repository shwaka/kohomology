import React from "react"

import type { Props } from "@theme/NavbarItem"

import { NavbarItemOnlyDevMode } from "./NavbarItemOnlyDevMode"

export function NavbarItemPlayground(props: Props): React.JSX.Element {
  return (
    <NavbarItemOnlyDevMode
      navbarItemProps={props}
      label="Playground"
      to="playground"
    />
  )
}
