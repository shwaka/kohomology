import { ReactElement } from "react";

import { render, screen } from "@testing-library/react"

import { ComplexAsTex } from "./target"

// This test was added to confirm that Jest can test React components.
test("ComplexAsTex", () => {
  render(<ComplexAsTex targetName="self"/>)
  const linkElement = screen.getByTestId("ComplexAsTex")
  expect(linkElement).toBeInTheDocument()
  expect(linkElement).toContainHTML("\\Lambda V")
})
