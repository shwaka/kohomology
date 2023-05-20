type OutputUpdateState = {
  command: "updateState"
  key: string
  value: unknown
}

type GetValueProperty<T extends OutputUpdateState, K> =
  T extends { key: infer L } ? (
    K extends L ? T["value"] : never
  ) : never

type KeyOfOutput<O extends { key: string, value: unknown }> = O extends { key: unknown } ? O["key"] : never

export type ExtractUpdateState<O> =
  O extends OutputUpdateState
    ? O
    : never

type _StateFromOutput<O extends OutputUpdateState> = {
  [K in KeyOfOutput<O>]: GetValueProperty<O, K>
}

export type StateFromOutput<O> =
  _StateFromOutput<ExtractUpdateState<O>>
