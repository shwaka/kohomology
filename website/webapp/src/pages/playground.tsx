import React from "react"

import { Playground } from "@site/src/components/Playground"
import Layout from "@theme/Layout"

export default function PlaygroundPage(): React.JSX.Element {
  return (
    <Layout
      title="Playground"
    >
      <Playground/>
    </Layout>
  )
}
