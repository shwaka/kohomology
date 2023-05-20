import { render, screen } from "@testing-library/react"
import React from "react"
import { TestApp } from "./__testutils__/TestApp"

test("WorkerContext", () => {
  render(<TestApp/>)
  const div = screen.getByTestId("my-component")
  const button = screen.getByTestId("add3")
  button.click()
  expect(div).toContainHTML("value=3")
})
