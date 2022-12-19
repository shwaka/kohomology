import { movingAverageOfNumberArrays } from "./movingAverage"

describe("movingAverageOfNumberArrays", () => {
  test("weight [1]", () => {
    const valueArray = [1, 2, 3]
    const weightArray = [1]
    expect(movingAverageOfNumberArrays({ valueArray, weightArray })).toEqual(valueArray)
  })
  test("weight [2]", () => {
    const valueArray = [1, 2, 3]
    const weightArray = [2]
    expect(movingAverageOfNumberArrays({ valueArray, weightArray })).toEqual(valueArray)
  })
  test("weight [1, 1]", () => {
    const valueArray = [1, 2, 3]
    const weightArray = [1, 1]
    expect(movingAverageOfNumberArrays({ valueArray, weightArray })).toEqual([1, 1.5, 2.5])
  })
  test("weight [4, 1]", () => {
    const valueArray = [1, 2, 3]
    const weightArray = [4, 1]
    expect(movingAverageOfNumberArrays({ valueArray, weightArray })).toEqual([1, 1.8, 2.8])
  })
})
