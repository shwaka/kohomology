import React from "react"

import Layout from "@theme/Layout"

import { Calculator } from "../calculator/Calculator"

export default function CalculatorPage(): React.JSX.Element {
  return (
    <Layout
      title="Calculator">
      <Calculator />
    </Layout>
  )
}
