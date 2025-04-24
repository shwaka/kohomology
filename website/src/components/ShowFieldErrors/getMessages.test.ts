import { FieldError } from "react-hook-form"
import { getMessages } from "./getMessages"

describe("getMessages with criteriaMode=firstError", () => {
  const criteriaMode = "firstError" as const

  test("single FieldError without types", () => {
    const fieldError: FieldError = { message: "an error message", type: "a type" }
    const fieldErrorArray: FieldError[] = [fieldError]
    expect(getMessages({ fieldErrorArray, criteriaMode })).toEqual([fieldError])
  })

  test("single FieldError with types", () => {
    // This can't happen in actual react-hook-form (?)
    const message = "an error message"
    const type = "a type"
    const fieldError: FieldError = {
      type: "",
      types: { [type]: message },
    }
    const fieldErrorArray: FieldError[] = [fieldError]
    expect(getMessages({ fieldErrorArray, criteriaMode })).toEqual([])
  })
})

describe("getMessages with criteriaMode=all", () => {
  const criteriaMode = "all" as const

  test("single FieldError without types", () => {
    const fieldError: FieldError = { message: "an error message", type: "a type" }
    const fieldErrorArray: FieldError[] = [fieldError]
    expect(getMessages({ fieldErrorArray, criteriaMode })).toEqual([fieldError])
  })

  test("single FieldError with types", () => {
    const message = "an error message"
    const type = "a type"
    const fieldError: FieldError = {
      type: "",
      types: { [type]: message },
    }
    const fieldErrorArray: FieldError[] = [fieldError]
    expect(getMessages({ fieldErrorArray, criteriaMode })).toEqual([{ message, type }])
  })
})
