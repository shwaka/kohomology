import { NavbarItemPlayground } from "@components/NavbarItems/NavbarItemPlayground"
import { GoToPublishedPage } from "@site/src/components/NavbarItems/GoToPublishedPage"
import ComponentTypes from "@theme-original/NavbarItem/ComponentTypes"

// https://github.com/facebook/docusaurus/issues/7227

export default {
  ...ComponentTypes,
  "custom-goToPublishedPage": GoToPublishedPage,
  "custom-playground": NavbarItemPlayground,
}
