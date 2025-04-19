import Layout from "@theme/Layout"
import React from "react"
import { Calculator } from "../components/Calculator"

export default function CalculatorPage(): React.JSX.Element {
  return (
    <Layout
      title="Calculator">
      <Calculator />
    </Layout>
  )
}
