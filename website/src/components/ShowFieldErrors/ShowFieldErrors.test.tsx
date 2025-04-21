import { render, screen, fireEvent } from "@testing-library/react"
import React from "react"
import { EmailForm } from "./__testutils__/EmailForm"

describe("EmailForm with ShowFieldErrors", () => {
  it("displays required field error", async () => {
    render(<EmailForm />)

    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText("This field is required")).toBeInTheDocument()
  })

  it("displays validation error when missing '@'", async () => {
    render(<EmailForm />)

    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "invalidemail" },
    })
    fireEvent.click(screen.getByText("Submit"))

    expect(await screen.findByText("Email must include '@'")).toBeInTheDocument()
  })

  // it("renders with showAllErrors=true (even if not using types)", async () => {
  //   render(<EmailForm showAllErrors />)
  //
  //   fireEvent.click(screen.getByText("Submit"))
  //
  //   expect(await screen.findByText("This field is required")).toBeInTheDocument()
  // })
})
