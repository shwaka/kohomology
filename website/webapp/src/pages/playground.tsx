import React, { ReactElement } from "react"

import { Playground } from "@site/src/components/Playground"
import Layout from "@theme/Layout"

export default function PlaygroundPage(): ReactElement {
  return (
    <Layout
      title="Playground"
    >
      <Playground/>
    </Layout>
  )
}
