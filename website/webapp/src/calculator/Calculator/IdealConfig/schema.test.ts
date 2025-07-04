import { z } from "zod/v4"

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
      expect(z.treeifyError(result.error).errors).toHaveLength(1)
      expect(z.treeifyError(result.error).errors).toContain("Please enter the generator.")
    }
  })

  it("should not accept non-empty string if validateGenerator returns error message", async () => {
    const errorMessage = "This is an error message."
    const validateGenerator = jest.fn().mockResolvedValue(errorMessage)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    const result = await idealGeneratorSchema.safeParseAsync("x++y")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(z.treeifyError(result.error).errors).toHaveLength(1)
      expect(z.treeifyError(result.error).errors).toContain(errorMessage)
    }
  })

  it("should not accept empty string if validateGenerator returns error message", async () => {
    const errorMessage = "This is an error message."
    const validateGenerator = jest.fn().mockResolvedValue(errorMessage)
    const idealGeneratorSchema = getIdealGeneratorTextSchema(validateGenerator)
    const result = await idealGeneratorSchema.safeParseAsync("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(z.treeifyError(result.error).errors).toHaveLength(2)
      expect(z.treeifyError(result.error).errors).toContain(errorMessage)
      expect(z.treeifyError(result.error).errors).toContain("Please enter the generator.")
    }
  })
})
