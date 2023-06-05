import { useLocation } from "@docusaurus/router"
import { render } from "@testing-library/react"
import React from "react"
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
