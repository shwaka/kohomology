interface URLQueryResultSuccess<T> {
  type: "success"
  value: T
}

interface URLQueryResultUnspecified {
  type: "unspecified"
}

interface URLQueryResultParseError {
  type: "error"
  message: string
}

/* eslint-disable @stylistic/operator-linebreak */
export type URLQueryResult<T> =
  | URLQueryResultSuccess<T>
  | URLQueryResultUnspecified
  | URLQueryResultParseError
/* eslint-enable @stylistic/operator-linebreak */
