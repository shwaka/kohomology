import { zodResolver } from "@hookform/resolvers/zod"
import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import React from "react"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { numberSchemaWithRequiredError } from "./numberSchemaWithRequiredError"

const errorMessage = "Please enter the degree."

const schema = z.object({
  degree: numberSchemaWithRequiredError(errorMessage),
})

type FormData = z.infer<typeof schema>

function TestForm({ valueAsNumber }: { valueAsNumber: boolean }): JSX.Element {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  /* eslint-disable @typescript-eslint/no-empty-function */
  return (
    <form onSubmit={handleSubmit(() => {})}>
      <input type="number" placeholder="degree" {...register("degree", { valueAsNumber })} />
      {errors.degree && <p role="alert">{errors.degree.message}</p>}
      <button type="submit">Submit</button>
    </form>
  )
}

describe("numberSchemaWithRequiredError with react-hook-form", () => {
  describe("registered with { valueAsNumber: true }", () => {
    it("should show the message given as the argument if the value is empty", async () => {
      render(<TestForm valueAsNumber={true}/>)

      fireEvent.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByRole("alert")).toHaveTextContent(errorMessage)
      })
    })

    it("should not show any error if the value is a number as a string", async () => {
      render(<TestForm valueAsNumber={true}/>)

      fireEvent.change(screen.getByPlaceholderText("degree"), {
        target: { value: "2" },
      })

      fireEvent.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.queryByRole("alert")).not.toBeInTheDocument()
      })
    })
  })

  describe("registered with { valueAsNumber: false }", () => {
    it("should show the message from z.number()", async () => {
      render(<TestForm valueAsNumber={false}/>)

      fireEvent.click(screen.getByText("Submit"))

      await waitFor(() => {
        expect(screen.getByRole("alert")).toHaveTextContent("Expected number, received string")
      })
    })
  })
})
