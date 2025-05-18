import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import React from "react"
import { OptionsButton, useOptionsButton } from "./OptionsButton"
import { MessageOption, MessageOptions } from "./options"

interface OptionsButtonContainerProps {
  options: MessageOptions
}

function OptionsButtonContainer({ options }: OptionsButtonContainerProps): React.JSX.Element {
  const { optionsButtonProps } = useOptionsButton("foo", options)
  return (<OptionsButton {...optionsButtonProps}/>)
}

describe("useOptionsButton", () => {
  test("single option", async () => {
    const user = userEvent.setup()
    const spy = jest.spyOn(navigator.clipboard, "writeText") // writeText is defined in userEvent.setup()

    const text = "Content to be copied"
    const label = "Copy this line"
    const option: MessageOption = { text, label }
    render(<OptionsButtonContainer options={[option]}/>)

    // Button in OptionsButton cannot be detected by getByRole("button") (why?)
    const button = screen.getByTestId("OptionsButton")
    await user.click(button)
    const menuItem = screen.getByText(label)
    await user.click(menuItem)

    expect(spy).toHaveBeenCalledOnceWith(text)
    spy.mockRestore()
  })
})
