import { Context, createContext, ReactNode, useCallback, useContext, useRef } from "react"
import { useSyncExternalStore } from "use-sync-external-store/shim" // for React < 18
import { WorkerContext } from "./WorkerContext"

export interface UseWorkerReturnValue<WI, WO> {
  postMessage: (message: WI) => void
  workerOutputLog: WO[]
  addListener: (key: string, onmessage: (workerOutput: WO) => void) => void
  restart: () => void
  addRestartListener: (key: string, onRestart: () => void) => void
}

export function useWorker<WI, WO>(
  context: WorkerContext<WI, WO>
): UseWorkerReturnValue<WI, WO> {
  const wrapper = useContext(context.reactContext)

  const subscribe = useCallback(
    (onStoreChange: () => void): (() => void) => {
      const key = "__useSyncExternalStore__"
      wrapper.subscribe(key, onStoreChange)
      return () => wrapper.unsubscribe(key)
    },
    [wrapper]
  )

  const getSnapshot = useCallback(
    () => {
      return wrapper.workerOutputLog
    },
    [wrapper]
  )

  const workerOutputLog: WO[] = useSyncExternalStore(subscribe, getSnapshot)

  const postMessage = useCallback(
    (workerInput: WI): void => {
      wrapper.postMessage(workerInput)
    },
    [wrapper]
  )

  const addListener = useCallback(
    (key: string, onmessage: (workerOutput: WO) => void): void => {
      wrapper.subscribe(key, onmessage)
    },
    [wrapper]
  )

  const addRestartListener = useCallback(
    (key: string, onRestart: () => void): void => {
      wrapper.subscribeRestart(key, onRestart)
    },
    [wrapper]
  )


  const restart = useCallback(
    (): void => wrapper.restart(),
    [wrapper]
  )

  return {
    postMessage,
    workerOutputLog,
    addListener,
    restart,
    addRestartListener,
  }
}
