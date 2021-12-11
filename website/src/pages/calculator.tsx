import React from "react"
import Layout from "@theme/Layout"
import { Calculator } from "../components/Calculator"

export default function CalculatorPage(): JSX.Element {
  return (
    <Layout
      title="Calculator">
      <Calculator />
    </Layout>
  )
}
