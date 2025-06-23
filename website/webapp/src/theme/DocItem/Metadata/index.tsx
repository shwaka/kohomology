import { type ReactNode } from "react"

import {useDoc} from "@docusaurus/plugin-content-docs/client"
import {PageMetadata} from "@docusaurus/theme-common"
import { getTitleWithDev } from "@theme/getTitleWithDev"

export default function DocItemMetadata(): ReactNode {
  const {metadata, frontMatter, assets} = useDoc()
  const title = getTitleWithDev(metadata.title)
  return (
    <PageMetadata
      title={title}
      description={metadata.description}
      keywords={frontMatter.keywords}
      image={assets.image ?? frontMatter.image}
    />
  )
}
