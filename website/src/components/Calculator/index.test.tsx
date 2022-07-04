import { fireEvent, getByRole, render, screen } from "@testing-library/react"
import React from "react"
import { Calculator } from "."

test("Calculator", () => {
  render(<Calculator/>)
  const calculator = screen.getByTestId("Calculator")
  expect(calculator).toContainHTML("Computation results will be shown here")
  expect(calculator).not.toContainHTML("Cohomology of ")
  const computeCohomologyForm = screen.getByTestId("ComputeCohomologyForm")
  expect(computeCohomologyForm).toContainHTML("Compute cohomology")
  const computeCohomologyButton = getByRole(computeCohomologyForm, "button")
  expect(computeCohomologyButton).toContainHTML("Compute")
  fireEvent.click(computeCohomologyButton)
  expect(calculator).toContainHTML("Cohomology of ")
})
