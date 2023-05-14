import { useLocation } from "@docusaurus/router"
import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import React from "react"
import { clickComputeCohomologyButton, expectComputeCohomologyButtonToContain, expectInitialState, expectResultsToContainHTML, selectComputationTarget } from "./__testutils__/utilsOnCalculator"
import { Calculator } from "."
import { InputJson } from "./__testutils__/InputJson"

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

test("Calculator", async () => {
  render(<Calculator/>)
  expectInitialState()
  // const calculator = screen.getByTestId("Calculator")
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Computing H^n(Λ(x, y), d) for",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{2} =\\ \\mathbb{Q}\\{[x]\\}"
    ],
  )
  expectComputeCohomologyButtonToContain("Compute")
})

test("input json", async () => {
  render(<Calculator/>)
  expectInitialState()
  // const calculator = screen.getByTestId("Calculator")
  // const results = getResultsDiv()
  const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
  await InputJson.inputValidJson(json)
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Computing H^n(Λ(x, y, z), d) for",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
  expectComputeCohomologyButtonToContain("Compute")
})

test("invalid json", async () => {
  render(<Calculator/>)
  expectInitialState()
  const json = "invalid json"
  await InputJson.inputInvalidJson(json)
  const dialog = screen.getByRole("dialog")
  expect(dialog).toContainHTML("Unexpected JSON token at offset 0")
  expect(dialog).toContainHTML(`JSON input: ${json}`)
})

test("compute cohomology of LX", async () => {
  render(<Calculator/>)
  expectInitialState()
  selectComputationTarget("freeLoopSpace")
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Computing H^n(Λ({x}, {y}, \\bar{x}, \\bar{y}), d) for",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{1} =\\ \\mathbb{Q}\\{[\\bar{x}]\\}",
      "H^{2} =\\ \\mathbb{Q}\\{[{x}]\\}",
    ]
  )
  expectComputeCohomologyButtonToContain("Compute")
})

test("url query", async () => {
  mockUseLocation.mockReturnValue({
    search: "?dgaJson=%5B%5B%22x%22%2C3%2C%22zero%22%5D%2C%5B%22y%22%2C3%2C%22zero%22%5D%2C%5B%22z%22%2C5%2C%22x+*+y%22%5D%5D"
  })
  render(<Calculator/>)
  expectInitialState()
  clickComputeCohomologyButton()
  expectResultsToContainHTML(
    [
      "Computing H^n(Λ(x, y, z), d) for",
      "H^{0} =\\ \\mathbb{Q}\\{[1]\\}",
      "H^{3} =\\ \\mathbb{Q}\\{[x],\\ [y]\\}",
    ]
  )
  expectComputeCohomologyButtonToContain("Compute")
})

test("url query with invalid json", async () => {
  mockUseLocation.mockReturnValue({
    search: "?dgaJson=invalidJson"
  })
  render(<Calculator/>)
  expectInitialState()
  expectResultsToContainHTML(
    [
      "[Error] Invalid JSON is given as URL parameter.",
    ]
  )
})
