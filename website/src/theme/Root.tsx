import Head from "@docusaurus/Head"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import React from "react"

// https://docusaurus.io/docs/swizzling#wrapper-your-site-with-root

export default function Root({children}: {children: React.ReactNode}): JSX.Element {
  const { siteConfig } = useDocusaurusContext()
  const baseUrl = siteConfig.baseUrl // baseUrl ends with "/"
  return (
    <>
      <Head>
        <link rel="apple-touch-icon" href={`${baseUrl}img/favicon300.png`} sizes="300x300"/>
      </Head>
      {children}
    </>
  )
}
