import { ReactElement } from "react"

import { render, screen } from "@testing-library/react"
import userEvent, { UserEvent } from "@testing-library/user-event"

import { MessageOption, MessageOptions } from "./options"
import { OptionsButton, useOptionsButton } from "./OptionsButton"

interface OptionsButtonContainerProps {
  options: MessageOptions
}

function OptionsButtonContainer({ options }: OptionsButtonContainerProps): ReactElement {
  const { optionsButtonProps } = useOptionsButton({
    containerClass: "foo", options,
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    showAll: () => {},
  })
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

  test("option with null text", async () => {
    const text = null
    const label = "Copy this line"
    const option: MessageOption = { text, label }
    render(<OptionsButtonContainer options={[option]}/>)

    const button = screen.getByTestId("OptionsButton")
    await user.click(button)
    const menuItem = screen.getByText(label)
    expect(menuItem).toHaveAttribute("aria-disabled", "true")
    // expect(menuItem).toBeDisabled()
  })
})
