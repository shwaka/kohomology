import { render, screen, fireEvent, renderHook, waitFor } from "@testing-library/react"
import React from "react"
import { EmailForm, EmailFormContainer, errorMessages, useEmailForm } from "./__testutils__/EmailForm"

describe("EmailForm with ShowFieldErrors", () => {
  it("displays required field error", async () => {
    // This test uses both renderHook and render.
    // Calling waitFor and rerender are necessary to apply change of result.current to EmailForm.
    // As in the other tests, using only render (for EmailFormContainer) is easier,
    // but this test is preserved for future reference.
    const { result } = renderHook(() => useEmailForm({}))
    const { rerender } = render(<EmailForm {...result.current}/>)

    fireEvent.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })
    rerender(<EmailForm {...result.current}/>)

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
  })

  it("displays validation error when missing '@'", async () => {
    render(<EmailFormContainer/>)

    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "invalidemail" },
    })
    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.includesAt)).toBeInTheDocument()
  })

  it("displays validation error when length is smaller than 3", async () => {
    render(<EmailFormContainer/>)

    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "a@" },
    })
    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.minLength)).toBeInTheDocument()
  })

  it("displays single error if showAllErrors=true and criteriaMode=firstError", async () => {
    render(
      <EmailFormContainer
        emailFormOptions={{ showAllErrors: true }}
        useEmailFormOptions={{ criteriaMode: "firstError" }}
      />
    )

    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
  })

  it("displays all errors if showAllErrors=true and criteriaMode=all", async () => {
    render(
      <EmailFormContainer
        emailFormOptions={{ showAllErrors: true }}
        useEmailFormOptions={{ criteriaMode: "all" }}
      />
    )

    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText(errorMessages.required)).toBeInTheDocument()
    expect(await screen.findByText(errorMessages.includesAt)).toBeInTheDocument()
    expect(await screen.findByText(errorMessages.minLength)).toBeInTheDocument()
  })
})
