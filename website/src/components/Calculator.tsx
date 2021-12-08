import React from "react"
import { computeCohomology } from "kohomology-js"

export function Calculator(): JSX.Element {
  return <div>{ computeCohomology("[[\"x\", 2, \"zero\"], [\"y\", 3, \"x^2\"]]", 10) }</div>
}
