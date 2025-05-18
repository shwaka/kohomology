import { render, screen } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"
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
  let user: UserEvent
  let writeTextSpy: jest.SpiedFunction<typeof navigator.clipboard.writeText>

  beforeEach(() => {
    user = userEvent.setup()
    // writeText is defined in userEvent.setup()
    writeTextSpy = jest.spyOn(navigator.clipboard, "writeText")
  })

  afterEach(() => {
    writeTextSpy.mockRestore()
  })

  test("single option", async () => {
    const text = "Content to be copied"
    const label = "Copy this line"
    const option: MessageOption = { text, label }
    render(<OptionsButtonContainer options={[option]}/>)

    // Button in OptionsButton cannot be detected by getByRole("button") (why?)
    const button = screen.getByTestId("OptionsButton")
    await user.click(button)
    const menuItem = screen.getByText(label)
    await user.click(menuItem)

    expect(writeTextSpy).toHaveBeenCalledOnceWith(text)
  })
})
