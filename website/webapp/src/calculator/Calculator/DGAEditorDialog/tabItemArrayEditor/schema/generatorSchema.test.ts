import { deegreeSchema, differentialValueSchema, generatorSchema, nameSchema } from "./generatorSchema"

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
      expect(result.error.flatten().formErrors).toContainEqual(
        expect.stringContaining("must start with alphabets")
      )
    }
  })

  for (const invalidChar of ["-", " ", ",", ".", "^", "{", "}"]) {
    it(`should not accept string containing "${invalidChar}"`, () => {
      const result = nameSchema.safeParse(`y${invalidChar}`)
      expect(result.success).toBe(false)
      if (!result.success) {
        expect(result.error.flatten().formErrors).toHaveLength(1)
        expect(result.error.flatten().formErrors).toContainEqual(
          expect.stringContaining("can only contain alphabets")
        )
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
