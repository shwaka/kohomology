import type { ReactNode } from "react"
import { Fragment } from "react"

import type { WrapperProps } from "@docusaurus/types"
import { getTitleWithDev } from "@theme/getTitleWithDev"
import type LayoutType from "@theme/Layout"
import Layout from "@theme-original/Layout"

type Props = WrapperProps<typeof LayoutType>

export default function LayoutWrapper(props: Props): ReactNode {
  const title = getTitleWithDev(props.title)
  return (
    <Fragment>
      <Layout {...props} title={title} />
    </Fragment>
  )
}
