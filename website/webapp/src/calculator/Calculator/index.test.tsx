
import { useLocation } from "@docusaurus/router"
import { render, screen, waitFor, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

import { Calculator } from "."
import { InputIdeal } from "./__testutils__/InputIdeal"
import { ApplyMethod, InputArray, InputJson } from "./__testutils__/InputJson"
import { clickComputeCohomologyButton, clickRestartButton, expectComputeCohomologyButtonToContain, waitForInitialState, expectSnackbarToContainHTML, selectComputationTarget, expectResultsToContainMessages } from "./__testutils__/utilsOnCalculator"

const mockUseLocation = useLocation as unknown as jest.Mock
beforeEach(() => {
  mockUseLocation.mockReturnValue({
    search: ""
  })
})

describe("basic features", () => {
  test("Calculator", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    // const calculator = screen.getByTestId("Calculator")
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n(Λ(x, y), d)$ for",
          "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
          "$H^{2} =\\  \\mathbb{Q}\\{ [x] \\}$"
        ],
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("restart", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await clickRestartButton(user)
    expectResultsToContainMessages(
      ["The background process is restarted"]
    )
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n(Λ(x, y), d)$ for",
          "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
          "$H^{2} =\\  \\mathbb{Q}\\{ [x] \\}$"
        ],
      )
    })
    expectComputeCohomologyButtonToContain("Compute")
  })
})

describe("array editor", () => {
  for (const applyMethod of (["button", "enter"] satisfies ApplyMethod[])) {
    test("add generator with applying through '${applyMethod}'", async () => {
      const user = userEvent.setup()
      render(<Calculator />)
      await waitForInitialState()
      await InputArray.addGeneratorAndApply(user, applyMethod)
      await clickComputeCohomologyButton(user)
      await waitFor(() => {
        expectResultsToContainMessages(
          [
            "Computing $H^n(Λ(x, y, z), d)$ for",
            "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
            "$H^{1} =\\  \\mathbb{Q}\\{ [z] \\}$",
            "$H^{2} =\\  \\mathbb{Q}\\{ [x] \\}$"
          ],
        )
      })
    })
  }
})

describe("input json", () => {
  test("valid json", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    // const calculator = screen.getByTestId("Calculator")
    // const results = getResultsDiv()
    const json = `[
  ["x", 3, "zero"],
  ["y", 3, "zero"],
  ["z", 5, "x * y"]
]`
    await InputJson.inputValidJson(user, json)
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n(Λ(x, y, z), d)$ for",
          "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
          "$H^{3} =\\  \\mathbb{Q}\\{ [x],\\  [y] \\}$",
        ]
      )
    })
    expectComputeCohomologyButtonToContain("Compute")
  })

  test("invalid json", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    const json = "invalid json"
    await InputJson.inputInvalidJson(user, json)
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Unexpected JSON token at offset 0")
    expect(dialog).toContainHTML(`JSON input: ${json}`)
  })
})

describe("freeLoopSpace", () => {
  test("compute cohomology of LX", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "freeLoopSpace")
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n(Λ({x}, {y}, \\bar{x}, \\bar{y}), d)$ for",
          "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
          "$H^{1} =\\  \\mathbb{Q}\\{ [\\bar{x}] \\}$",
          "$H^{2} =\\  \\mathbb{Q}\\{ [{x}] \\}$",
        ]
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })
})

describe("idealQuot", () => {
  test("compute cohomology of ΛV/I", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "idealQuot")
    await InputIdeal.inputValidIdealGenerator(user, ["x"])
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n((Λ(x, y), d)/\\mathrm{DGIdeal}(x))$ for $0 \\leq n \\leq 20$",
          "$H^{0} =\\  \\mathbb{Q}\\{ [[1]] \\}$",
          "$H^{2} =\\  0$",
          "$H^{3} =\\  \\mathbb{Q}\\{ [[y]] \\}$",
        ],
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("ideal not closed under d", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(user, ["y"])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("d(y)=x^2 must be contained in the ideal Ideal(y) to define dg ideal.")
  })

  test("empty ideal generator", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(user, [""], true)
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Please enter the generator.")
  })

  test("invalid generator of DGA", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(user, ["a"])
    const dialog = screen.getByRole("dialog")
    expect(dialog).toContainHTML("Invalid generator name: a")
    expect(dialog).toContainHTML("Valid names are: x, y")
  })

  test("parse failure", async () => {
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState()
    await selectComputationTarget(user, "idealQuot")
    await InputIdeal.inputInvalidIdealGenerator(user, ["+"])
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
    const user = userEvent.setup()
    render(<Calculator />)
    await waitForInitialState("$(\\Lambda V, d) =  (\\Lambda( x,\\  y,\\  z ), d)$")
    await clickComputeCohomologyButton(user)
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          "Computing $H^n(Λ(x, y, z), d)$ for",
          "$H^{0} =\\  \\mathbb{Q}\\{ [1] \\}$",
          "$H^{3} =\\  \\mathbb{Q}\\{ [x],\\  [y] \\}$",
        ]
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
  })

  test("url query with invalid json", async () => {
    mockUseLocation.mockReturnValue({
      search: "?dgaJson=invalidJson"
    })
    render(<Calculator />)
    await waitForInitialState()
    await waitFor(() => {
      expectSnackbarToContainHTML(
        [
          "[Error] Invalid JSON is given as URL parameter.",
        ]
      )
    })
  })

  test("copy to clipboard", async () => {
    const user = userEvent.setup()
    const writeTextSpy = jest.spyOn(navigator.clipboard, "writeText") // after userEvent.setup()

    render(<Calculator />)
    await waitForInitialState()
    await clickComputeCohomologyButton(user)

    const expectedText = "$H^{2} =\\  \\mathbb{Q}\\{ [x] \\}$"
    await waitFor(() => {
      expectResultsToContainMessages(
        [
          expectedText
        ],
      )
      expectComputeCohomologyButtonToContain("Compute")
    })
    const messageDivList: HTMLElement[] = screen.getAllByTestId("show-styled-message")
    const filtered: HTMLElement[] = messageDivList.filter((element) =>
      element.getAttribute("data-styled-message") === expectedText
    )
    expect(filtered).toHaveLength(1)
    const messageDiv: HTMLElement = filtered[0]

    const button = within(messageDiv).getByTestId("OptionsButton")
    await user.click(button)
    const menuItem = screen.getByText("Copy this line")
    await user.click(menuItem)
    expect(writeTextSpy).toHaveBeenCalledOnceWith(expectedText)

    writeTextSpy.mockRestore()
  })
})
