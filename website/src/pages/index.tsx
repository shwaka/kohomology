import Link from "@docusaurus/Link"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import CodeBlock from "@theme/CodeBlock"
import Layout from "@theme/Layout"
import clsx from "clsx"
import React from "react"
import HomepageFeatures from "../components/HomepageFeatures"
import { ImportKotlin } from "../components/ImportKotlin"
import styles from "./index.module.css"
// import useBaseUrl from "@docusaurus/useBaseUrl"

function HomepageHeader(): React.JSX.Element {
  const {siteConfig} = useDocusaurusContext()
  // const dokkaUrl = useBaseUrl("/dokka/index.html")
  // const benchUrl = useBaseUrl("/benchmark/index.html")
  return (
    <header className={clsx("hero hero--primary", styles.heroBanner)}>
      <div className="container">
        <h1 className="hero__title">{siteConfig.title}</h1>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="/docs/intro">
            Documentation
          </Link>
        </div>
      </div>
    </header>
  )
}

export default function Home(): React.JSX.Element {
  const {siteConfig} = useDocusaurusContext()
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="A library to compute the cohomology of Sullivan algebras">
      <HomepageHeader />
      <main>
        <HomepageFeatures />
        <div className={styles.exampleColumnContainer}>
          <ImportKotlin
            path="TopPageExample.kt"
            restrict={true}
            className={clsx("col col--4", styles.exampleColumn)}/>
          <div className={clsx("col col--4", styles.exampleColumn)}>
            This code prints:
            <CodeBlock>
              {`H^0 = Q[[1]]
H^1 = Q[]
H^2 = Q[[a], [b]]
H^3 = Q[]
H^4 = Q[]
H^5 = Q[[- ay + bx], [- az + by]]
H^6 = Q[]
H^7 = Q[[- a^2z + aby]]
H^8 = Q[]
H^9 = Q[]`}
            </CodeBlock>
          </div>
        </div>
      </main>
    </Layout>
  )
}
