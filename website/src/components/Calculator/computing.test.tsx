import { useLocation } from "@docusaurus/router"
import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import React from "react"
import { clickComputeCohomologyButton, expectInitialState, expectResultsToContainHTML, getComputeCohomologyButton } from "./__testutils__/utilsOnCalculator"
import { Calculator } from "."

const mockUseLocation = useLocation as unknown as jest.Mock
mockUseLocation.mockReturnValue({
  search: ""
})

describe("'computing' shown on the 'compute' button", () => {
  it("disappears after computation finished", async () => {
    render(<Calculator/>)
    expectInitialState()
    const computeCohomologyButton = getComputeCohomologyButton()
    expect(computeCohomologyButton).toContainHTML("Compute")
  })
})
