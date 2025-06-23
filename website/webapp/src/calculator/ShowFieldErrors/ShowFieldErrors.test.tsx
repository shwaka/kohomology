
import { render, screen, renderHook, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

import { EmailForm, EmailFormContainer, errorMessages, useEmailForm } from "./__testutils__/EmailForm"

describe("EmailForm with ShowFieldErrors", () => {
  it("displays required field error", async () => {
    const user = userEvent.setup()
    // This test uses both renderHook and render.
    // Calling waitFor and rerender are necessary to apply change of result.current to EmailForm.
    // As in the other tests, using only render (for EmailFormContainer) is easier,
    // but this test is preserved for future reference.
    const { result } = renderHook(() => useEmailForm({}))
    const { rerender } = render(<EmailForm {...result.current}/>)

    await user.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })
    rerender(<EmailForm {...result.current}/>)

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
  })

  it("displays validation error when missing '@'", async () => {
    const user = userEvent.setup()
    render(<EmailFormContainer/>)

    await user.type(screen.getByPlaceholderText("Email"), "invalidemail")
    await user.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.includesAt)).toBeInTheDocument()
  })

  it("displays validation error when length is smaller than 3", async () => {
    const user = userEvent.setup()
    render(<EmailFormContainer/>)

    await user.type(screen.getByPlaceholderText("Email"), "a@")
    await user.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.minLength)).toBeInTheDocument()
  })

  it("displays single error if criteriaModeForComponent=all and criteriaModeForHook=firstError", async () => {
    const user = userEvent.setup()
    render(
      <EmailFormContainer
        emailFormOptions={{ criteriaModeForComponent: "all" }}
        useEmailFormOptions={{ criteriaModeForHook: "firstError" }}
      />
    )

    await user.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
  })

  it("displays all errors if criteriaModeForComponent=all and criteriaModeForHook=all", async () => {
    const user = userEvent.setup()
    render(
      <EmailFormContainer
        emailFormOptions={{ criteriaModeForComponent: "all" }}
        useEmailFormOptions={{ criteriaModeForHook: "all" }}
      />
    )

    await user.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
    expect(await screen.findByText(errorMessages.includesAt)).toBeInTheDocument()
    expect(await screen.findByText(errorMessages.minLength)).toBeInTheDocument()
  })
})
