export interface MyWorkerInput {
  value: number
}

export type MyWorkerOutput = {
  result: string
}

export type MyWorkerState = {
  value: number
}

export type MyWorkerFunc = {
  add: (value: number) => number
}
