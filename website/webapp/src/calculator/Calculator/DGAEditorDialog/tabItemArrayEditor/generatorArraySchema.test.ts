import { magicMessageToHideError } from "@calculator/ShowFieldErrors"
import { z } from "zod"

import { formValueSchema, generatorArraySchema } from "./generatorArraySchema"

function getErrorsByPath(
  error: z.ZodError<unknown>,
  path: (string | number)[]
): z.ZodIssue[] {
  return error.issues.filter(
    issue => JSON.stringify(issue.path) === JSON.stringify(path)
  )
}

function getErrorMessagesByPath(
  error: z.ZodError<unknown>,
  path: (string | number)[]
): string[] {
  return getErrorsByPath(error, path).map((issue) => issue.message)
}

describe("generatorArraySchema", () => {
  it("should sccept the model of 2-sphere", () => {
    const result = generatorArraySchema.safeParse([
      { name: "x", degree: 2, differentialValue: "0" },
      { name: "y", degree: 3, differentialValue: "x^2" },
    ])
    expect(result.success).toBe(true)
  })

  it("should not accept if the differentialValue has illegal degree", () => {
    const result = generatorArraySchema.safeParse([
      { name: "x", degree: 2, differentialValue: "0" },
      { name: "y", degree: 3, differentialValue: "x" },
    ])
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors[1]).toHaveLength(1)
      expect(result.error.flatten().fieldErrors[1]).toContain(
        "The degree of d(y) is expected to be deg(y)+1=4, but the given value x has degree 2."
      )
    }
  })

  it("should show useful message for the error at the beginning", () => {
    const result = generatorArraySchema.safeParse([
      { name: "x", degree: 2, differentialValue: "." },
    ])
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors[0]).toHaveLength(1)
      expect(result.error.flatten().fieldErrors[0]).toContainEqual(
        expect.stringContaining("No matching token at the beginning")
      )
    }
  })

  it("currently does not show useful message for the error after +", () => {
    const result = generatorArraySchema.safeParse([
      { name: "x", degree: 2, differentialValue: "+" },
    ])
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors[0]).toHaveLength(1)
      expect(result.error.flatten().fieldErrors[0]).toContainEqual(
        expect.stringContaining("AlternativesFailure(errors=[")
      )
    }
  })
})

describe("formValueSchema", () => {
  it("should accept the model of 2-sphere", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: 3, differentialValue: "x^2" },
      ],
    })
    expect(result.success).toBe(true)
  })

  it("should not accept when both positive and negative degrees exist", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: -3, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors._global_errors).toContain(
        "Cannot mix generators of positive and negative degrees."
      )
    }
  })

  it("should not accept if names are duplicated", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "x", degree: 3, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 0, "name"])).toContain(
        magicMessageToHideError
      )
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 1, "name"])).toContain(
        "Generator names must be unique, but x is already used."
      )
    }
  })

  it("should not accept if names are duplicated", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: 3, differentialValue: "0" },
        { name: "x", degree: 3, differentialValue: "0" },
        { name: "x", degree: 3, differentialValue: "0" },
        { name: "z", degree: 3, differentialValue: "0" },
        { name: "y", degree: 3, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 0, "name"])).toContain(
        magicMessageToHideError
      )
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 1, "name"])).toContain(
        magicMessageToHideError
      )
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 2, "name"])).toContain(
        "Generator names must be unique, but x is already used."
      )
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 3, "name"])).toContain(
        "Generator names must be unique, but x is already used."
      )
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 4, "name"])).toBeEmpty()
      expect(getErrorMessagesByPath(result.error, ["generatorArray", 5, "name"])).toContain(
        "Generator names must be unique, but y is already used."
      )
    }
  })

  it("should not accept if there are both positive and negative degrees", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: -2, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors._global_errors).toHaveLength(1)
      expect(result.error.flatten().fieldErrors._global_errors).toContain(
        "Cannot mix generators of positive and negative degrees."
      )
    }
  })
})
