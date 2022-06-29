import useBaseUrl from "@docusaurus/useBaseUrl"
import Layout from "@theme/Layout"
import React from "react"

export default function APIPage(): JSX.Element {
  const url = useBaseUrl("dokka/index.html")
  return (
    <Layout
      title="API">
      <iframe
        src={url}
        width="100%"
        style={{ height: "80vh" }}
      />
    </Layout>
  )
}
