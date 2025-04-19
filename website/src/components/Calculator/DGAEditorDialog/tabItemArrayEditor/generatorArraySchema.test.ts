import { formValueSchema, generatorArraySchema } from "./generatorArraySchema"

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
      expect(result.error.flatten().fieldErrors.generatorArray).toContain(
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
      expect(result.error.flatten().fieldErrors.generatorArray).toHaveLength(1)
      expect(result.error.flatten().fieldErrors.generatorArray).toContain(
        'Generator names must be unique. Duplicated names are "x"'
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
      expect(result.error.flatten().fieldErrors.generatorArray).toHaveLength(1)
      expect(result.error.flatten().fieldErrors.generatorArray).toContain(
        "Cannot mix generators of positive and negative degrees."
      )
    }
  })

  it("should show useful message for the error at the beginning", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "." },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.generatorArray).toHaveLength(1)
      expect(result.error.flatten().fieldErrors.generatorArray).toContainEqual(
        expect.stringContaining("No matching token at the beginning")
      )
    }
  })

  it("currently does not show useful message for the error after +", () => {
    const result = formValueSchema.safeParse({
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "+" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.generatorArray).toHaveLength(1)
      expect(result.error.flatten().fieldErrors.generatorArray).toContainEqual(
        expect.stringContaining("AlternativesFailure(errors=[")
      )
    }
  })
})
