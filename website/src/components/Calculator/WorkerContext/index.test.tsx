import React from "react"
import { render, screen } from "@testing-library/react"
import { TestApp } from "./__testutils__/TestApp"

test("WorkerContext", () => {
  render(<TestApp/>)
  const div = screen.getByTestId("my-component")
  expect(div).toContainHTML("value=3")
})
