import { DSV, dgaDsvToJson, dgaJsonToDsv } from "./dotSeparatedValues"

test("DSV.parse", () => {
  const dsv = "x.2.0.y.3.x^2"
  expect(DSV.parse(dsv)).toEqual([
    "x", "2", "0",
    "y", "3", "x^2"
  ])
})

test("DSV.stringify", () => {
  const value = [
    "x", "2", "0",
    "y", "3", "x^2"
  ]
  expect(DSV.stringify(value)).toBe("x.2.0.y.3.x^2")
})

test("dgaJsonToDsv with valid json", () => {
  const json = `[
  ["x", 2, "0"],
  ["y", 3, "x^2"]
]`
  expect(dgaJsonToDsv(json)).toBe("x.2.0.y.3.x^2")
})

test("dgaDsvToJson with valid dsv", () => {
  const dsv = "x.2.0.y.3.x^2"
  expect(dgaDsvToJson(dsv)).toBe('[["x",2,"0"],["y",3,"x^2"]]')
})
