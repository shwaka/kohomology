import { useCallback, useContext, useEffect } from "react"
import { useSyncExternalStore } from "use-sync-external-store/shim" // for React < 18
import { WorkerContext } from "./WorkerContext"
import { MessageInput, MessageOutput, MessageOutputUpdateState } from "./expose"

function isUpdateState<WO, WS>(output: MessageOutput<WO, WS>): output is MessageOutputUpdateState<WS> {
  return (output.type === "updateState")
}

function getPartialStateFromOutput<WO, WS>(output: MessageOutput<WO, WS>): Partial<WS> | null {
  if (!isUpdateState(output)){
    return null
  }
  return {
    [output.key]: output.value
  } as Partial<WS>
}

export interface UseWorkerReturnValue<WI, WO, WS> {
  postMessage: (message: WI) => void
  workerOutputLog: MessageOutput<WO, WS>[]
  addListener: (key: string, onmessage: (workerOutput: WO) => void) => void
  restart: () => void
  addRestartListener: (key: string, onRestart: () => void) => void
  state: WS
}

export function useWorker<WI, WO, WS>(
  context: WorkerContext<WI, WO, WS>
): UseWorkerReturnValue<WI, WO, WS> {
  const wrapper = useContext(context.reactContext)
  const [state, setState] = useContext(context.stateContext)
  useEffect(() => {
    wrapper.subscribe("__set_worker_state__", (workerOutput: MessageOutput<WO, WS>): void => {
      const partialState: Partial<WS> | null = getPartialStateFromOutput(workerOutput)
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

  const workerOutputLog: MessageOutput<WO, WS>[] = useSyncExternalStore(subscribe, getSnapshot)

  const postMessage = useCallback(
    (workerInput: WI): void => {
      const message: MessageInput<WI> = {
        type: "input",
        value: workerInput,
      }
      wrapper.postMessage(message)
    },
    [wrapper]
  )

  const addListener = useCallback(
    (key: string, onmessage: (workerOutput: WO) => void): void => {
      wrapper.subscribe(
        key,
        (output) => {
          if (output.type === "output") {
            onmessage(output.value)
          }
        }
      )
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
