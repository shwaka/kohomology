import { DSV } from "./DSV"

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
