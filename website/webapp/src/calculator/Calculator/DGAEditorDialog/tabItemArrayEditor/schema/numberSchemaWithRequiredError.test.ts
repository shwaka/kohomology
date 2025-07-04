import { z } from "zod/v4"

import { numberSchemaWithRequiredError } from "./numberSchemaWithRequiredError"

describe("numberSchemaWithRequiredError", () => {
  const errorMessage = "number expected"
  const errorMessageForString = "Invalid input: expected number, received string"

  for (const value of [0, 1, 2, -1, 1.1, 0.5, -2.3]) {
    it(`should successfully parse ${value}`, () => {
      const result = numberSchemaWithRequiredError(errorMessage).safeParse(value)
      expect(result.success).toBe(true)
    })
  }

  it("should fail to parse NaN", () => {
    const result = numberSchemaWithRequiredError(errorMessage).safeParse(NaN)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(z.treeifyError(result.error).errors).toContain(errorMessage)
    }
  })

  it("should fail to parse empty string", () => {
    const result = numberSchemaWithRequiredError(errorMessage).safeParse("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(z.treeifyError(result.error).errors).toContain(errorMessageForString)
    }
  })

  it("should fail to parse non-empty string", () => {
    const result = numberSchemaWithRequiredError(errorMessage).safeParse("foo")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(z.treeifyError(result.error).errors).toContain(errorMessageForString)
    }
  })
})
