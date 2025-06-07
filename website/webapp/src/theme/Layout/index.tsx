import type { WrapperProps } from "@docusaurus/types"
import { isDevelopmentMode } from "@site/src/utils/isDevelopmentMode"
import type LayoutType from "@theme/Layout"
import Layout from "@theme-original/Layout"
import React, { type ReactNode } from "react"

type Props = WrapperProps<typeof LayoutType>

function getTitleWithDev(title: string | undefined): string | undefined {
  if (title === undefined) {
    return undefined
  } else if (isDevelopmentMode()) {
    return `[dev] ${title}`
  } else {
    return title
  }
}

export default function LayoutWrapper(props: Props): ReactNode {
  const title = getTitleWithDev(props.title)
  return (
    <React.Fragment>
      <Layout {...props} title={title}/>
    </React.Fragment>
  )
}
