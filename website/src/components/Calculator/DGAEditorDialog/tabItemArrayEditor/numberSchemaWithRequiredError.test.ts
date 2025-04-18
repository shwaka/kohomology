import { numberSchemaWithRequiredError } from "./numberSchemaWithRequiredError"

describe("numberSchemaWithRequiredError", () => {
  const errorMessage = "number expected"

  for (const value of [0, 1, 2, -1]) {
    it(`should successfully parse ${value}`, () => {
      const result = numberSchemaWithRequiredError(errorMessage).safeParse(value)
      expect(result.success).toBe(true)
    })
  }

  it("should fail to parse NaN", () => {
    const result = numberSchemaWithRequiredError(errorMessage).safeParse(NaN)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toContain(errorMessage)
    }
  })

  it("should fail to parse empty string", () => {
    const result = numberSchemaWithRequiredError(errorMessage).safeParse("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toContain(errorMessage)
    }
  })
})
