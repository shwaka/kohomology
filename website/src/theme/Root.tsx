import Head from "@docusaurus/Head"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import React, { useEffect } from "react"
import { isDevelopmentMode } from "../utils/isDevelopmentMode"

// https://docusaurus.io/docs/swizzling#wrapper-your-site-with-root

export default function Root({children}: {children: React.ReactNode}): React.JSX.Element {
  const { siteConfig } = useDocusaurusContext()
  const baseUrl = siteConfig.baseUrl // baseUrl ends with "/"

  useEffect(() => {
    // Set data attribute: <html data-build-env="dev" ...>
    // This is used ins src/css/custom.scss
    // I don't know why, but the following did not work (overridden by docusaurus?)
    // - document.documentElement.classList.add("dev-mode")
    // - document.body.classList.add("dev-mode")
    const key = "data-build-env"
    if (isDevelopmentMode()) {
      document.documentElement.setAttribute(key, "dev")
    } else {
      document.documentElement.setAttribute(key, "prod")
    }
    // Since Root never unmounts, we do not need cleanup functions.
  }, [])

  return (
    <React.Fragment>
      <Head>
        <link rel="icon" href={`${baseUrl}img/favicon.ico`}/>
        <link rel="apple-touch-icon" href={`${baseUrl}img/favicon.png`} sizes="300x300"/>
      </Head>
      {children}
    </React.Fragment>
  )
}
