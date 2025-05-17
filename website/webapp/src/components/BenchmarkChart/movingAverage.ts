export function movingAverageOfNumberArrays(
  { valueArray, weightArray }: {
    valueArray: number[]
    weightArray: number[]
  }
): number[] {
  const averageArray: number[] = []
  for (let i = 0; i < valueArray.length; i++) {
    if (i + 1 < weightArray.length) {
      let average = 0
      for (let j = 0; j <= i; j++) {
        average += valueArray[i - j] * weightArray[j]
      }
      const weightSum = weightArray.slice(0, i + 1).reduce((a, b) => a + b, 0)
      averageArray.push(average / weightSum)
    } else {
      let average = 0
      for (let j = 0; j < weightArray.length; j++) {
        average += valueArray[i - j] * weightArray[j]
      }
      const weightSum = weightArray.reduce((a, b) => a + b, 0)
      averageArray.push(average / weightSum)
    }
  }
  return averageArray
}

export function movingAverage<T>(
  { dataArray, weightArray, getValue, getDataWithNewValue }: {
    dataArray: T[]
    weightArray: number[]
    getValue: (data: T) => number
    getDataWithNewValue: (data: T, newValue: number) => T
  }
): T[] {
  const valueArray = dataArray.map(getValue)
  const newValueArray = movingAverageOfNumberArrays({ valueArray, weightArray })
  const result: T[] = []
  for (let i = 0; i < dataArray.length; i++) {
    result.push(getDataWithNewValue(dataArray[i], newValueArray[i]))
  }
  return result
}
