import { movingAverage, movingAverageOfNumberArrays } from "./movingAverage"

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

type Data = {
  name: string
  value: number
}

describe("movingAverage", () => {
  test("weight [4, 1]", () => {
    const dataArray: Data[] = [
      { name: "foo", value: 1 },
      { name: "bar", value: 2 },
      { name: "baz", value: 3 },
    ]
    const weightArray = [4, 1]
    const getValue = (data: Data): number => data.value
    const getDataWithNewValue = (data: Data, newValue: number): Data => ({
      ...data,
      value: newValue,
    })
    expect(
      movingAverage({ dataArray, weightArray, getValue, getDataWithNewValue })
    ).toEqual([
      { name: "foo", value: 1 },
      { name: "bar", value: 1.8 },
      { name: "baz", value: 2.8 },
    ])
  })
})
