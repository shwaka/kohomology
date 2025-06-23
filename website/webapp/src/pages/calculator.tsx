import { ReactElement } from "react"

import Layout from "@theme/Layout"

import { Calculator } from "../calculator/Calculator"

export default function CalculatorPage(): ReactElement {
  return (
    <Layout
      title="Calculator">
      <Calculator />
    </Layout>
  )
}
