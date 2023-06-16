import { dsvToJson, jsonToDsv } from "./dotSeparatedValues"

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
