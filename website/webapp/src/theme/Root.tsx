import React, { useEffect, ReactElement } from "react"

import Head from "@docusaurus/Head"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"

import { isDevelopmentMode } from "../utils/isDevelopmentMode"

// https://docusaurus.io/docs/swizzling#wrapper-your-site-with-root

function removeIndexHtml(): void {
  // See https://github.com/shwaka/kohomology/issues/315
  if (typeof window !== "undefined") {
    const path = window.location.pathname
    if (path.endsWith("/index.html")) {
      const replacedPath: string = path.replace(/\/index\.html$/, "")
      const pathToRedirect = (replacedPath === "") ? "/" : replacedPath
      window.location.replace(pathToRedirect)
    }
  }
}

function setBuildEnv(): void {
  // Set data attribute: <html data-build-env="dev" ...>
  // This is used in src/css/custom.scss
  // I don't know why, but the following did not work (overridden by docusaurus?)
  // - document.documentElement.classList.add("dev-mode")
  // - document.body.classList.add("dev-mode")
  const key = "data-build-env"
  if (isDevelopmentMode()) {
    document.documentElement.setAttribute(key, "dev")
  } else {
    document.documentElement.setAttribute(key, "prod")
  }
}

export default function Root({children}: {children: React.ReactNode}): ReactElement {
  const { siteConfig } = useDocusaurusContext()
  const baseUrl = siteConfig.baseUrl // baseUrl ends with "/"

  useEffect(() => {
    setBuildEnv()
    // Since Root never unmounts, we do not need cleanup functions.
  }, [])

  useEffect(() => {
    removeIndexHtml()
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
