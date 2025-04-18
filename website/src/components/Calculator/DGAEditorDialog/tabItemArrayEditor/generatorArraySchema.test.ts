import { deegreeSchema, differentialValueSchema, formValueSchema, generatorArraySchema, generatorSchema, nameSchema } from "./generatorArraySchema"

describe("nameSchema", () => {
  for (const name of ["x", "x_", "x1", "xyz", "y_z", "_y", "a", "α", "π_123"]) {
    it(`should accept "${name}"`, () => {
      const result = nameSchema.safeParse(name)
      expect(result.success).toBe(true)
    })
  }

  it("should not accept empty string", () => {
    const result = nameSchema.safeParse("")
    expect(result.success).toBe(false)
    if (!result.success) {
      // This example has two errors. This may be a bug?
      expect(result.error.flatten().formErrors).toHaveLength(2)
      expect(result.error.flatten().formErrors).toContain("Please enter the name.")
      expect(result.error.flatten().formErrors).toContain("Identifier name must be non-empty.")
    }
  })

  it("should not accept string starting with number", () => {
    const result = nameSchema.safeParse("1y")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(1)
      expect(result.error.flatten().formErrors).toContainEqual(expect.stringContaining("must start with alphabets"))
    }
  })

  for (const invalidChar of ["-", " ", ",", ".", "^", "{", "}"]) {
    it(`should not accept string containing "${invalidChar}"`, () => {
      const result = nameSchema.safeParse(`y${invalidChar}`)
      expect(result.success).toBe(false)
      if (!result.success) {
        expect(result.error.flatten().formErrors).toHaveLength(1)
        expect(result.error.flatten().formErrors).toContainEqual(expect.stringContaining("can only contain alphabets"))
      }
    })
  }
})

describe("degreeSchema", () => {
  for (const degree of [1, 10, -1]) {
    it(`should accept ${degree}`, () => {
      const result = deegreeSchema.safeParse(degree)
      expect(result.success).toBe(true)
    })
  }

  it("should not accept 0", () => {
    const result = deegreeSchema.safeParse(0)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(1)
      expect(result.error.flatten().formErrors).toContain("The degree cannot be 0.")
    }
  })
})

describe("differentialValueSchema", () => {
  // Currently, differentialValueSchema accepts ANY non-empty string.
  for (const value of ["x", "x+y", "x + y", "x-y", "2*x", "0", "1", "ad-bc", "a.b!?"]) {
    it(`should accept "${value}"`, () => {
      const result = differentialValueSchema.safeParse(value)
      expect(result.success).toBe(true)
    })
  }

  it("should not accept empty string", () => {
    const result = differentialValueSchema.safeParse("")
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().formErrors).toHaveLength(1)
      expect(result.error.flatten().formErrors).toContain("Please enter the value of the differential.")
    }
  })
})

describe("generatorSchema", () => {
  it("should accept valid value", () => {
    const result = generatorSchema.safeParse({ name: "x", degree: 1, differentialValue: "0" })
    expect(result.success).toBe(true)
  })

  it("should not accept if degree=0", () => {
    const result = generatorSchema.safeParse({ name: "x", degree: 0, differentialValue: "0" })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.degree).toContain("The degree cannot be 0.")
    }
  })
})

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
      dummy: "dummy",
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "y", degree: 3, differentialValue: "x^2" },
      ],
    })
    expect(result.success).toBe(true)
  })

  it("should not accept when both positive and negative degrees exist", () => {
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

  it("should not accept if names are duplicated", () => {
    const result = formValueSchema.safeParse({
      dummy: "dummy",
      generatorArray: [
        { name: "x", degree: 2, differentialValue: "0" },
        { name: "x", degree: 3, differentialValue: "0" },
      ],
    })
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.dummy).toHaveLength(1)
      expect(result.error.flatten().fieldErrors.dummy).toContain(
        'Generator names must be unique. Duplicated names are "x"'
      )
    }
  })

  it("should show useful message for the error at the beginning", () => {
    const result = formValueSchema.safeParse({
      dummy: "dummy",
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
      dummy: "dummy",
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
