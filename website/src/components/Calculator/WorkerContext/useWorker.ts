import { useCallback, useContext, useEffect } from "react"
import { useSyncExternalStore } from "use-sync-external-store/shim" // for React < 18
import { WorkerContext } from "./WorkerContext"
import { MessageInput, MessageInputCallFunc, MessageOutput, MessageOutputUpdateState, WFBase } from "./expose"

function isUpdateState<WO, WS, WF extends WFBase>(output: MessageOutput<WO, WS, WF>): output is MessageOutputUpdateState<WS> {
  return (output.type === "updateState")
}

function getPartialStateFromOutput<WO, WS, WF extends WFBase>(output: MessageOutput<WO, WS, WF>): Partial<WS> | null {
  if (!isUpdateState(output)){
    return null
  }
  return {
    [output.key]: output.value
  } as Partial<WS>
}

export type RunAsync<WF extends WFBase> =
  <K extends keyof WF>(key: K, args: Parameters<WF[K]>) => Promise<ReturnType<WF[K]>>

export interface UseWorkerReturnValue<WI, WO, WS, WF extends WFBase> {
  postMessage: (message: WI) => void
  workerOutputLog: MessageOutput<WO, WS, WF>[]
  addListener: (key: string, onmessage: (workerOutput: WO) => void) => void
  restart: () => void
  addRestartListener: (key: string, onRestart: () => void) => void
  state: WS
  runAsync: RunAsync<WF>
}

export function useWorker<WI, WO, WS, WF extends WFBase>(
  context: WorkerContext<WI, WO, WS, WF>
): UseWorkerReturnValue<WI, WO, WS, WF> {
  const wrapper = useContext(context.reactContext)
  const [state, setState] = useContext(context.stateContext)
  useEffect(() => {
    wrapper.subscribe("__set_worker_state__", (workerOutput: MessageOutput<WO, WS, WF>): void => {
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

  const workerOutputLog: MessageOutput<WO, WS, WF>[] = useSyncExternalStore(subscribe, getSnapshot)

  const postMessage = useCallback(
    (workerInput: WI): void => {
      const message: MessageInput<WI, WF> = {
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

  const runAsync: RunAsync<WF> = useCallback(
    async <K extends keyof WF> (key: K, args: Parameters<WF[K]>): Promise<ReturnType<WF[K]>> => {
      const id = generateId()
      const input: MessageInputCallFunc<WF> = {
        type: "callFunc",
        key, args, id,
      }
      const subscribeKey = `runAsync-${id}`
      // The type parameter ReturnType<WF[K]> is necessary (not inferred if omitted)
      const promise = new Promise<ReturnType<WF[K]>>((resolve, _reject) => {
        // wrapper.subscribe must be called BEFORE wrapper.postMessage
        // since wrapper.postMessage immediately submits the output in test environment.
        wrapper.subscribe(
          subscribeKey,
          (output) => {
            if ((output.type === "funcResult") && (output.key === key) && (output.id === id)) {
              wrapper.unsubscribe(subscribeKey)
              resolve(output.result)
            }
          }
        )
      })
      wrapper.postMessage(input)
      return promise
    },
    [wrapper]
  )

  return {
    postMessage,
    workerOutputLog,
    addListener,
    restart,
    addRestartListener,
    state,
    runAsync,
  }
}

const characters = "0123456789abcdef"

function generateId(): string {
  const n = 20
  const charactersLength = characters.length
  const resultArray: string[] = []
  for (let i = 0; i < n; i++) {
    resultArray.push(characters.charAt(Math.floor(Math.random() * charactersLength)))
  }
  return resultArray.join("")
}
