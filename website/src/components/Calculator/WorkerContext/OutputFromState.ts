export type OutputFromState<S, K = keyof S> = K extends keyof S ? {
  command: "updateState"
  key: K
  value: S[K]
} : never
