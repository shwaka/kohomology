import { ReactElement } from "react";

import { render, screen, renderHook, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { MultipleFieldErrors } from "react-hook-form"

import { EmailForm, errorMessages, useEmailForm } from "./__testutils__/EmailForm"

// This is not a test for ShowFieldErrors (or any other component in this project).
// This test is added to confirm behavior of react-hook-form and document it.

describe("behavior of react-hook-form expected by ShowFieldErrors", () => {
  test("empty string", async () => {
    const user = userEvent.setup()
    const { result } = renderHook(() => useEmailForm({}))
    render(<EmailForm {...result.current}/>)

    await user.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })

    // The first error "required" is thrown.
    expect(result.current.errors.email?.message).toBe(errorMessages.required)
    expect(result.current.errors.email?.type).toBe("required")

    // The other errors ("includesAt", "minLength") are ignored.
    expect(result.current.errors.email?.types).toBeUndefined()
  })

  test("string not containing @", async () => {
    const user = userEvent.setup()
    const { result } = renderHook(() => useEmailForm({}))
    render(<EmailForm {...result.current}/>)

    await user.type(screen.getByPlaceholderText("Email"), "invalidemail")
    await user.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })

    expect(result.current.errors.email?.message).toBe(errorMessages.includesAt)
    expect(result.current.errors.email?.type).toBe("includesAt")
    expect(result.current.errors.email?.types).toBeUndefined()
  })

  test("empty string with criteriaModeForHook=all", async () => {
    const user = userEvent.setup()
    const { result } = renderHook(() => useEmailForm({ criteriaModeForHook: "all" }))
    render(<EmailForm {...result.current}/>)

    await user.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })

    expect(result.current.errors.email?.message).toBe(errorMessages.required)
    expect(result.current.errors.email?.type).toBe("required")

    const types: MultipleFieldErrors | undefined = result.current.errors.email?.types
    expect(types).not.toBeUndefined()
    if (types === undefined) { throw Error("This can't happen!") }
    expect(Object.keys(types).sort()).toEqual(["required", "includesAt", "minLength"].sort())
    expect(result.current.errors.email?.types?.required).toBe(errorMessages.required)
    expect(result.current.errors.email?.types?.includesAt).toBe(errorMessages.includesAt)
    expect(result.current.errors.email?.types?.minLength).toBe(errorMessages.minLength)
  })

  test("string not containing @ with criteriaModeForHook=all", async () => {
    const user = userEvent.setup()
    const { result } = renderHook(() => useEmailForm({ criteriaModeForHook: "all" }))
    render(<EmailForm {...result.current}/>)

    await user.type(screen.getByPlaceholderText("Email"), "invalidemail")
    await user.click(screen.getByText("Submit"))
    await waitFor(() => {
      expect(Object.keys(result.current.errors)).not.toHaveLength(0)
    })

    expect(result.current.errors.email?.message).toBe(errorMessages.includesAt)
    expect(result.current.errors.email?.type).toBe("includesAt")

    const types: MultipleFieldErrors | undefined = result.current.errors.email?.types
    expect(types).not.toBeUndefined()
    if (types === undefined) { throw Error("This can't happen!") }
    expect(Object.keys(types).sort()).toEqual(["includesAt"].sort())
    expect(result.current.errors.email?.types?.includesAt).toBe(errorMessages.includesAt)
  })
})
