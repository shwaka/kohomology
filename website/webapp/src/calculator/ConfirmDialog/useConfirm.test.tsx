import React, { useState } from "react"

import { render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

import { useConfirm } from "./useConfirm"

const prompt = "Do you really set text?"
const defaultText = "This is default."
const newText = "This is new text."

function ConfirmContainer(): React.JSX.Element {
  const { confirm, confirmDialog } = useConfirm(
    { trueText: "Yes", falseText: "No" }
  )
  const [text, setText] = useState(defaultText)
  async function tryToSetText(newText: string): Promise<void> {
    const answer: boolean = await confirm(prompt)
    if (answer) {
      setText(newText)
    }
  }
  return (
    <div>
      <div data-testid="text-div">
        {text}
      </div>
      <button onClick={async () => await tryToSetText(newText)}>
        Set text
      </button>
      {confirmDialog}
    </div>
  )
}

describe("useConfirm", () => {
  test("confirm answering yes", async () => {
    const user = userEvent.setup()
    render(<ConfirmContainer/>)
    const textDiv = screen.getByTestId("text-div")

    const setTextButton = screen.getByText("Set text")
    await user.click(setTextButton)
    expect(textDiv).toContainHTML(defaultText)

    const dialog = screen.getByRole("dialog")
    const yesButton = within(dialog).getByText("Yes")
    await user.click(yesButton)
    await waitForElementToBeRemoved(dialog)
    expect(textDiv).toContainHTML(newText)
  })

  test("confirm answering no", async () => {
    const user = userEvent.setup()
    render(<ConfirmContainer/>)
    const textDiv = screen.getByTestId("text-div")

    const setTextButton = screen.getByText("Set text")
    await user.click(setTextButton)
    expect(textDiv).toContainHTML(defaultText)

    const dialog = screen.getByRole("dialog")
    const noButton = within(dialog).getByText("No")
    await user.click(noButton)
    await waitForElementToBeRemoved(dialog)
    expect(textDiv).toContainHTML(defaultText)
  })

  test("confirm answering no by clicking backdrop", async () => {
    const user = userEvent.setup()
    render(<ConfirmContainer/>)
    const textDiv = screen.getByTestId("text-div")

    const setTextButton = screen.getByText("Set text")
    await user.click(setTextButton)
    expect(textDiv).toContainHTML(defaultText)

    const dialog = screen.getByRole("dialog")
    // eslint-disable-next-line testing-library/no-node-access
    const backdrop: Element | null = document.querySelector(".MuiBackdrop-root")
    expect(backdrop).toBeTruthy()
    if (backdrop === null) {
      throw new Error(
        "This can't happen since backdrop is already expected to be truthy!"
      )
    }
    await user.click(backdrop)
    await waitForElementToBeRemoved(dialog)
    expect(textDiv).toContainHTML(defaultText)
  })
})
