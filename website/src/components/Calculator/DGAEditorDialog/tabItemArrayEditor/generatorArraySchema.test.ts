import { formValueSchema, generatorArraySchema, generatorSchema } from "./generatorArraySchema"

describe("generatorSchema", () => {
  it("should success for valid value", () => {
    const result = generatorSchema.safeParse({ name: "x", degree: 1, differentialValue: "0" })
    expect(result.success).toBe(true)
  })

  it("should fail when degree=0", () => {
    const result = generatorSchema.safeParse({ name: "x", degree: 0, differentialValue: "0" })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.degree).toContain("The degree cannot be 0.")
    }
  })
})

describe("generatorArraySchema", () => {
  it("should success for valid value", () => {
    const result = generatorArraySchema.safeParse([
      { name: "x", degree: 2, differentialValue: "0" },
      { name: "y", degree: 3, differentialValue: "x^2" },
    ])
    expect(result.success).toBe(true)
  })
})

describe("formValueSchema", () => {
  it("should success for valid value", () => {
    const result = formValueSchema.safeParse({
      dummy: "dummy",
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: 3, differentialValue: "x^2" },
      ],
    })
    expect(result.success).toBe(true)
  })

  it("should fail when both positive and negative degrees exist", () => {
    const result = formValueSchema.safeParse({
      dummy: "dummy",
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: -3, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.dummy).toContain("Cannot mix generators of positive and negative degrees.")
    }
  })
})
