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

export type URLQueryResult<T> =
  | URLQueryResultSuccess<T>
  | URLQueryResultUnspecified
  | URLQueryResultParseError
