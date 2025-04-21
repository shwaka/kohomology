import { render, screen, fireEvent, renderHook, waitFor } from "@testing-library/react"
import React from "react"
import { EmailForm, EmailFormContainer, useEmailForm } from "./__testutils__/EmailForm"

describe("EmailForm with ShowFieldErrors", () => {
  it("displays required field error", async () => {
    const { result } = renderHook(() => useEmailForm())
    const { rerender } = render(<EmailForm {...result.current}/>)

    fireEvent.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })
    rerender(<EmailForm {...result.current}/>)

    expect(await screen.findByText("This field is required")).toBeInTheDocument()
  })

  it("displays validation error when missing '@'", async () => {
    render(<EmailFormContainer/>)

    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "invalidemail" },
    })
    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText("Email must include '@'")).toBeInTheDocument()
  })

  it("displays validation error when length is smaller than 3", async () => {
    render(<EmailFormContainer/>)

    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "a@" },
    })
    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText("Email must be at least 3 characters")).toBeInTheDocument()
  })

  // it("renders with showAllErrors=true (even if not using types)", async () => {
  //   render(<EmailForm showAllErrors/>)
  //
  //   fireEvent.click(screen.getByText("Submit"))
  //
  //   expect(await screen.findByText("This field is required")).toBeInTheDocument()
  // })
})
