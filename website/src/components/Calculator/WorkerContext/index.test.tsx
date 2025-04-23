import { render, screen, act } from "@testing-library/react"
import React from "react"
import { TestApp } from "./__testutils__/TestApp"

test("WorkerContext", () => {
  render(<TestApp/>)
  const divLog = screen.getByTestId("show-workerOutputLog")
  const divLogFromListener = screen.getByTestId("show-log-from-listener")
  const divStateValue = screen.getByTestId("show-state-value")
  const button = screen.getByTestId("add3")
  act(() => button.click())
  expect(divLog).toContainHTML("value=3")
  expect(divLogFromListener).toContainHTML("value=3")
  expect(divStateValue).toContainHTML("stateValue=3")
  act(() => button.click())
  expect(divLog).toContainHTML("value=6")
  expect(divLogFromListener).toContainHTML("value=6")
  expect(divStateValue).toContainHTML("stateValue=6")
  const runAsyncButton = screen.getByTestId("runAsync-add5")
  act(() => runAsyncButton.click())
  // Since runAsync does not submit MyWorkerOutput, log is not printed here.
  // expect(divLog).toContainHTML("value=11")
  // expect(divLogFromListener).toContainHTML("value=11")
  expect(divStateValue).toContainHTML("stateValue=11")
})
