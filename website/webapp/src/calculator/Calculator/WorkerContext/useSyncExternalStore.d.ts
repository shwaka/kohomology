declare module "use-sync-external-store/shim" {
  function useSyncExternalStore<T>(
    subscribe: (onStoreChange: () => void) => (() => void),
    getSnapshot: () => T,
  ): T
  export { useSyncExternalStore }
}
