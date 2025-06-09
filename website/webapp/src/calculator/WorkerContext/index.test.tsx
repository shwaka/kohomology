import React from "react"

import { render, screen, act, waitFor } from "@testing-library/react"

import { TestApp } from "./__testutils__/TestApp"

describe("WorkerContext", () => {
  test("postMessage", async () => {
    render(<TestApp/>)
    const divLog = screen.getByTestId("show-workerOutputLog")
    const divLogFromListener = screen.getByTestId("show-log-from-listener")
    const divStateValue = screen.getByTestId("show-state-value")
    const button = screen.getByTestId("add3")

    act(() => button.click())
    await waitFor(() => {
      expect(divLog).toContainHTML("value=3")
    })
    expect(divLogFromListener).toContainHTML("value=3")
    expect(divStateValue).toContainHTML("stateValue=3")

    act(() => button.click())
    await waitFor(() => {
      expect(divLog).toContainHTML("value=6")
    })
    expect(divLogFromListener).toContainHTML("value=6")
    expect(divStateValue).toContainHTML("stateValue=6")
  })

  test("runAsync", async () => {
    render(<TestApp/>)
    const divStateValue = screen.getByTestId("show-state-value")
    const divRunAsyncResult = screen.getByTestId("show-runAsyncResult")
    const runAsyncButton = screen.getByTestId("runAsync-add5")

    // This await is necessary since onClick of runAsyncButton is async function
    await act(async () => runAsyncButton.click())
    await waitFor(() => {
      expect(divStateValue).toContainHTML("stateValue=5")
    })
    expect(divRunAsyncResult).toContainHTML("runAsyncResult=5")
    // Since runAsync does not submit MyWorkerOutput, log is not printed here.
    // expect(divLog).toContainHTML("value=11")
    // expect(divLogFromListener).toContainHTML("value=11")

    await act(async () => runAsyncButton.click())
    await waitFor(() => {
      expect(divStateValue).toContainHTML("stateValue=10")
    })
    expect(divRunAsyncResult).toContainHTML("runAsyncResult=10")
  })
})
