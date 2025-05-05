import { useLocation } from "@docusaurus/router"
import { render, screen, waitFor } from "@testing-library/react"
import React from "react"
import { InputIdeal } from "./__testutils__/InputIdeal"
import { InputArray, InputJson } from "./__testutils__/InputJson"
import { clickComputeCohomologyButton, clickRestartButton, expectComputeCohomologyButtonToContain, waitForInitialState, expectResultsToContainHTML, expectSnackbarToContainHTML, selectComputationTarget } from "./__testutils__/utilsOnCalculator"
import { Calculator } from "."

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

describe("basic features", () => {
  test("Calculator", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    // const calculator = screen.getByTestId("Calculator")
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ(x, y), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{2} =\\ $ $\\mathbb{Q}\\{$ $[x]$ $\\}$"
        ],
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("restart", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    await clickRestartButton()
    expectResultsToContainHTML(
      ["The background process is restarted"]
    )
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ(x, y), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{2} =\\ $ $\\mathbb{Q}\\{$ $[x]$ $\\}$"
        ],
      )
    })
    expectComputeCohomologyButtonToContain("Compute")
  })
})

describe("array editor", () => {
  test("add generator", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    await InputArray.addGeneratorAndApply("button")
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ(x, y, z), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{1} =\\ $ $\\mathbb{Q}\\{$ $[z]$ $\\}$",
          "$H^{2} =\\ $ $\\mathbb{Q}\\{$ $[x]$ $\\}$"
        ],
      )
    })
  })
})

describe("input json", () => {
  test("valid json", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    // const calculator = screen.getByTestId("Calculator")
    // const results = getResultsDiv()
    const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
    await InputJson.inputValidJson(json)
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ(x, y, z), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{3} =\\ $ $\\mathbb{Q}\\{$ $[x],\\ $ $[y]$ $\\}$",
        ]
      )
    })
    expectComputeCohomologyButtonToContain("Compute")
  })

  test("invalid json", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    const json = "invalid json"
    await InputJson.inputInvalidJson(json)
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Unexpected JSON token at offset 0")
    expect(dialog).toContainHTML(`JSON input: ${json}`)
  })
})

describe("freeLoopSpace", () => {
  test("compute cohomology of LX", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("freeLoopSpace")
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ({x}, {y}, \\bar{x}, \\bar{y}), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{1} =\\ $ $\\mathbb{Q}\\{$ $[\\bar{x}]$ $\\}$",
          "$H^{2} =\\ $ $\\mathbb{Q}\\{$ $[{x}]$ $\\}$",
        ]
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })
})

describe("idealQuot", () => {
  test("compute cohomology of ΛV/I", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("idealQuot")
    await InputIdeal.inputValidIdealGenerator(["x"])
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n((Λ(x, y), d)/\\mathrm{DGIdeal}(x))$ for $0 \\leq n \\leq 20$",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[[1]]$ $\\}$",
          "$H^{2} =\\ $ $0$",
          "$H^{3} =\\ $ $\\mathbb{Q}\\{$ $[[y]]$ $\\}$",
        ],
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("ideal not closed under d", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(["y"])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("d(y)=x^2 must be contained in the ideal Ideal(y) to define dg ideal.")
  })

  test("empty ideal generator", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("idealQuot")
    await InputIdeal.inputInvalidIdealGenerator([""])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Please enter the generator.")
  })

  test("invalid generator of DGA", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(["a"])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Invalid generator name: a")
    expect(dialog).toContainHTML("Valid names are: x, y")
  })

  test("parse failure", async () => {
    render(<Calculator/>)
    await waitForInitialState()
    selectComputationTarget("idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(["+"])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Could not parse input: AlternativesFailure")
  })
})

describe("url query", () => {
  test("url query with valid json", async () => {
    // Λ(x,y,z), dx=dy=0, dz=xy
    mockUseLocation.mockReturnValue({
      search: "?dgaJson=%5B%5B%22x%22%2C3%2C%22zero%22%5D%2C%5B%22y%22%2C3%2C%22zero%22%5D%2C%5B%22z%22%2C5%2C%22x+*+y%22%5D%5D"
    })
    render(<Calculator/>)
    await waitForInitialState("$(\\Lambda V, d) = $ $(\\Lambda($ $x,\\ $ $y,\\ $ $z$ $), d)$")
    clickComputeCohomologyButton()
    await waitFor(() => {
      expectResultsToContainHTML(
        [
          "Computing $H^n(Λ(x, y, z), d)$ for",
          "$H^{0} =\\ $ $\\mathbb{Q}\\{$ $[1]$ $\\}$",
          "$H^{3} =\\ $ $\\mathbb{Q}\\{$ $[x],\\ $ $[y]$ $\\}$",
        ]
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("url query with invalid json", async () => {
    mockUseLocation.mockReturnValue({
      search: "?dgaJson=invalidJson"
    })
    render(<Calculator/>)
    await waitForInitialState()
    await waitFor(() => {
      expectSnackbarToContainHTML(
        [
          "[Error] Invalid JSON is given as URL parameter.",
        ]
      )
    })
  })
})
