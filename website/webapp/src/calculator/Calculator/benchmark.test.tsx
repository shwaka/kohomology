import React, { ReactElement } from "react"

import { useLocation } from "@docusaurus/router"
import { render } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

import { Calculator } from "."
import { InputJson } from "./__testutils__/InputJson"
import { waitForInitialState } from "./__testutils__/utilsOnCalculator"

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

test("renderCalculator", async () => {
  render(<Calculator/>)
  await waitForInitialState()
})

test("inputJson", async () => {
  const user = userEvent.setup()
  render(<Calculator/>)
  await waitForInitialState()
  const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
  await InputJson.inputValidJson(user, json)
})
