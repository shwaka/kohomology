import { useCallback, useContext, useEffect } from "react"
import { useSyncExternalStore } from "use-sync-external-store/shim" // for React < 18
import { ExtractUpdateState, StateFromOutput } from "./StateFromOutput"
import { WorkerContext } from "./WorkerContext"

function isUpdateState<WO>(output: WO): output is ExtractUpdateState<WO> {
  return ("command" in output) &&
    ((output as unknown as { command: unknown }).command === "updateState")
}

function getPartialStateFromOutput<WO>(output: WO): StateFromOutput<WO> | null {
  if (!isUpdateState(output)){
    return null
  }
  return {
    [output.key]: output.value
  } as StateFromOutput<WO>
}

export interface UseWorkerReturnValue<WI, WO> {
  postMessage: (message: WI) => void
  workerOutputLog: WO[]
  addListener: (key: string, onmessage: (workerOutput: WO) => void) => void
  restart: () => void
  addRestartListener: (key: string, onRestart: () => void) => void
  state: StateFromOutput<WO>
}

export function useWorker<WI, WO>(
  context: WorkerContext<WI, WO>
): UseWorkerReturnValue<WI, WO> {
  const wrapper = useContext(context.reactContext)
  const [state, setState] = useContext(context.stateContext)
  useEffect(() => {
    wrapper.subscribe("__set_worker_state__", (workerOutput: WO): void => {
      const partialState: StateFromOutput<WO> | null = getPartialStateFromOutput(workerOutput)
      if (partialState === null) {
        return
      }
      setState((previousState) => ({
        ...previousState,
        ...partialState,
      }))
    })
  }, [wrapper, setState])

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
    state,
  }
}
