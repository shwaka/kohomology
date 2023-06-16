import { DSV, dsvToJson, jsonToDsv } from "./dotSeparatedValues"

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

test("jsonToDsv with valid json", () => {
  const json = `[
  ["x", 2, "0"],
  ["y", 3, "x^2"]
]`
  expect(jsonToDsv(json)).toBe("x.2.0.y.3.x^2")
})

test("dsvToJson with valid dsv", () => {
  const dsv = "x.2.0.y.3.x^2"
  expect(dsvToJson(dsv)).toBe('[["x",2,"0"],["y",3,"x^2"]]')
})
