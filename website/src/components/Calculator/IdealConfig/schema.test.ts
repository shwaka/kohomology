import { getIdealGeneratorTextSchema } from "./schema"

describe("getIdealGeneratorTextSchema", () => {
  it("should accept non-empty string if validateGenerator returns true", async () => {
    const validateGenerator = jest.fn().mockResolvedValue(true)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    for (const text of ["x", "y", "x+y", "x * y", "0"]) {
      const result = await idealGeneratorSchema.safeParseAsync(text)
      expect(result.success).toBe(true)
    }
  })

  it("should not accept empty string even if validateGenerator returns true", async () => {
    const validateGenerator = jest.fn().mockResolvedValue(true)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    const result = await idealGeneratorSchema.safeParseAsync("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(1)
      expect(result.error.flatten().formErrors).toContain("Please enter the generator.")
    }
  })

  it("should not accept non-empty string if validateGenerator returns error message", async () => {
    const errorMessage = "This is an error message."
    const validateGenerator = jest.fn().mockResolvedValue(errorMessage)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    const result = await idealGeneratorSchema.safeParseAsync("x++y")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(1)
      expect(result.error.flatten().formErrors).toContain(errorMessage)
    }
  })

  it("should not accept empty string if validateGenerator returns error message", async () => {
    const errorMessage = "This is an error message."
    const validateGenerator = jest.fn().mockResolvedValue(errorMessage)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    const result = await idealGeneratorSchema.safeParseAsync("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(2)
      expect(result.error.flatten().formErrors).toContain(errorMessage)
      expect(result.error.flatten().formErrors).toContain("Please enter the generator.")
    }
  })
})
