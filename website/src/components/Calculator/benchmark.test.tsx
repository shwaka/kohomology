import { useLocation } from "@docusaurus/router"
import { render } from "@testing-library/react"
import React from "react"
import { InputJson } from "./__testutils__/InputJson"
import { Calculator } from "."

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

test("renderCalculator", async () => {
  render(<Calculator/>)
})

test("inputJson", async () => {
  render(<Calculator/>)
  const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
  await InputJson.inputValidJson(json)
})
