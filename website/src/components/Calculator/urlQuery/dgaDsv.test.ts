import { dgaDsvToJson, dgaJsonToDsv } from "./dgaDsv"

describe("dgaJsonToDsv", () => {
  it("should return correct dsv for valid json", () => {
    const json = `[
  ["x", 2, "0"],
  ["y", 3, "x^2"]
]`
    expect(dgaJsonToDsv(json)).toBe("x.2.0.y.3.x^2")
  })

  it("should return null for invalid json", () => {
    const json = `[
  ["x", 2, "0"],
  ["y", 3, null]
]`
    expect(dgaJsonToDsv(json)).toBe(null)
  })
})

describe("dgaDsvToJson", () => {
  it("should return correct json for valid dsv", () => {
    const dsv = "x.2.0.y.3.x^2"
    expect(dgaDsvToJson(dsv)).toBe('[["x",2,"0"],["y",3,"x^2"]]')
  })

  it("should throw an error for invalid dsv", () => {
    const dsv = "x.2.0.y.3"
    expect(() => {
      dgaDsvToJson(dsv)
    }).toThrow("must be divisible by 3")
  })
})
