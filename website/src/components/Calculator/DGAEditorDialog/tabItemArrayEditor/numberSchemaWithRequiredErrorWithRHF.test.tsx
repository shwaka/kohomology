import { zodResolver } from "@hookform/resolvers/zod"
import { render, screen, waitFor } from "@testing-library/react"
import React, { useState } from "react"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { numberSchemaWithRequiredError } from "./numberSchemaWithRequiredError"
import userEvent from "@testing-library/user-event"

const errorMessage = "Please enter the degree."

const schema = z.object({
  degree: numberSchemaWithRequiredError(errorMessage),
})

type FormData = z.infer<typeof schema>

function TestForm({ valueAsNumber }: { valueAsNumber: boolean }): React.JSX.Element {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  // If successfully submitted (without any error), then setSubmitted(true).
  // If submission contains an error, then setSubmitted is not called.
  const [submitted, setSubmitted] = useState(false)

  return (
    <form onSubmit={handleSubmit(() => { setSubmitted(true) })}>
      <input type="number" placeholder="degree" {...register("degree", { valueAsNumber })} />
      {errors.degree && <p role="alert">{errors.degree.message}</p>}
      <button type="submit">Submit</button>
      {submitted && <span data-testid="submitted">submitted</span>}
    </form>
  )
}

describe("numberSchemaWithRequiredError with react-hook-form", () => {
  // Note: numberSchemaWithRequiredError must be used with { valueAsNumber: true }
  describe("registered with { valueAsNumber: true }", () => {
    it("should show the message given as the argument if the value is empty", async () => {
      const user = userEvent.setup()
      render(<TestForm valueAsNumber={true}/>)

      await user.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByRole("alert")).toHaveTextContent(errorMessage)
      })
      expect(screen.queryByTestId("submitted")).not.toBeInTheDocument()
    })

    it("should not show any error if the value is a number as a string", async () => {
      const user = userEvent.setup()
      render(<TestForm valueAsNumber={true}/>)

      await user.type(screen.getByPlaceholderText("degree"), "2")
      await user.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByTestId("submitted")).toBeInTheDocument()
      })
      expect(screen.queryByRole("alert")).not.toBeInTheDocument()
    })
  })

  describe("registered with { valueAsNumber: false }", () => {
    it("should show the message from z.number() if the value is the empty string", async () => {
      const user = userEvent.setup()
      render(<TestForm valueAsNumber={false}/>)

      await user.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByRole("alert")).toHaveTextContent("Expected number, received string")
      })
      expect(screen.queryByTestId("submitted")).not.toBeInTheDocument()
    })

    it("should show the message from z.number() if the value is a number as a string", async () => {
      // This does NOT work since { valueAsNumber: false }
      const user = userEvent.setup()
      render(<TestForm valueAsNumber={false}/>)

      await user.type(screen.getByPlaceholderText("degree"), "2")
      await user.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByRole("alert")).toHaveTextContent("Expected number, received string")
      })
      expect(screen.queryByTestId("submitted")).not.toBeInTheDocument()
    })
  })
})
