import { render, screen } from "@testing-library/react"
import React from "react"
import { Calculator } from "."

test("Calculator", () => {
  render(<Calculator/>)
  const calculator = screen.getByTestId("Calculator")
  expect(calculator).toContainHTML("Computation results will be shown here")
})
