import { idealDsvToJson, idealJsonToDsv } from "./idealDsv"

test("idealJsonToDsv with valid json", () => {
  const json = '["x^2", "y"]'
  expect(idealJsonToDsv(json)).toBe("x^2.y")
})

test("idealDsvToJson with valid dsv", () => {
  const dsv = "x^2.y"
  expect(idealDsvToJson(dsv)).toBe('["x^2","y"]')
})
