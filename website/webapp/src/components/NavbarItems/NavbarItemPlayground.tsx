import { ReactElement } from "react";

import type { Props } from "@theme/NavbarItem"

import { NavbarItemOnlyDevMode } from "./NavbarItemOnlyDevMode"

export function NavbarItemPlayground(props: Props): ReactElement {
  return (
    <NavbarItemOnlyDevMode
      navbarItemProps={props}
      label="Playground"
      to="playground"
    />
  )
}
