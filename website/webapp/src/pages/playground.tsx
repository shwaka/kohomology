import { Playground } from "@site/src/components/Playground"
import Layout from "@theme/Layout"
import React from "react"

export default function PlaygroundPage(): React.JSX.Element {
  return (
    <Layout
      title="Playground"
    >
      <Playground/>
    </Layout>
  )
}
