import { useLocation } from "@docusaurus/router"
import { render } from "@testing-library/react"
import React from "react"
import { InputIdeal } from "./__testutils__/InputIdeal"
import { expectInitialState, selectComputationTarget } from "./__testutils__/utilsOnCalculator"
import { Calculator } from "."

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

test("compute cohomology of ΛV/I", async () => {
  render(<Calculator/>)
  expectInitialState()
  selectComputationTarget("idealQuot")
  await InputIdeal.inputValidIdealGenerator(["x"])
})
