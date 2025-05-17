import { idealDsvToJson, idealJsonToDsv } from "./idealDsv"

describe("idealJsonToDsv", () => {
  it("should return correct dsv for valid json", () => {
    const json = '["x^2", "y"]'
    expect(idealJsonToDsv(json)).toBe("x^2.y")
  })

  it("should return null for invalid json", () => {
    const json = '["x^2", 1]'
    expect(idealJsonToDsv(json)).toBe(null)
  })
})

describe("idealDsvToJson", () => {
  it("should return correct json for valid dsv", () => {
    const dsv = "x^2.y"
    expect(idealDsvToJson(dsv)).toBe('["x^2","y"]')
  })
})
