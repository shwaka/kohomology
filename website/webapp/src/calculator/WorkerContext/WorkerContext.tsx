import { Context, createContext, ReactNode, useRef, useState, ReactElement, Dispatch, SetStateAction } from "react"

import { WFBase } from "./expose"
import { WorkerWrapper } from "./WorkerWrapper"

type OmitIfEmpty<T, K extends string | number | symbol> =
  T extends { [_ in K]: infer S }
    ? (Record<string, never> extends S ? Omit<T, K> : T)
    : T

interface ProviderProps<WS> {
  defaultState: WS
  children: ReactNode
}

type StateAndSetState<WS> = [
  WS,
  Dispatch<SetStateAction<WS>>,
]

type StateContext<WS> = Context<StateAndSetState<WS>>

export type WorkerContext<WI, WO, WS, WF extends WFBase> = {
  reactContext: Context<WorkerWrapper<WI, WO, WS, WF>>
  stateContext: StateContext<WS>
  Provider: (props: OmitIfEmpty<ProviderProps<WS>, "defaultState">) => ReactElement
}

function WorkerContextProvider<WI, WO, WS, WF extends WFBase>(
  props: {
    context: Context<WorkerWrapper<WI, WO, WS, WF>>
    stateContext: StateContext<WS>
    createWorker: () => Worker
  } & ProviderProps<WS>
): ReactElement {
  const wrapperRef = useRef<WorkerWrapper<WI, WO, WS, WF> | null>(null)
  if (wrapperRef.current === null) {
    wrapperRef.current = new WorkerWrapper(props.createWorker)
  }
  const stateAndSetState = useState<WS>(props.defaultState)

  const CurrentContext = props.context
  const StateContext = props.stateContext

  return (
    <CurrentContext.Provider value={wrapperRef.current}>
      <StateContext.Provider value={stateAndSetState}>
        {props.children}
      </StateContext.Provider>
    </CurrentContext.Provider>
  )
}

function createProvider<WI, WO, WS, WF extends WFBase>(
  reactContext: Context<WorkerWrapper<WI, WO, WS, WF>>,
  stateContext: StateContext<WS>,
  createWorker: () => Worker,
): ((props: OmitIfEmpty<ProviderProps<WS>, "defaultState">) => ReactElement) {
  const WorkerContextProviderCurried = (props: OmitIfEmpty<ProviderProps<WS>, "defaultState">): ReactElement => {
    // If props does not contain defaultState, then WS is empty.
    const defaultState: WS =
      "defaultState" in props ? props.defaultState : ({} as WS)
    return (
      <WorkerContextProvider
        context={reactContext}
        stateContext={stateContext}
        createWorker={createWorker}
        defaultState={defaultState}
      >
        {props.children}
      </WorkerContextProvider>
    )
  }
  return WorkerContextProviderCurried
}

export function createWorkerContext<WI, WO, WS, WF extends WFBase>(
  createWorker: () => Worker,
): WorkerContext<WI, WO, WS, WF> {
  const reactContext = createContext<WorkerWrapper<WI, WO, WS, WF>>(WorkerWrapper.default())
  const stateContext = createContext<StateAndSetState<WS>>([
    undefined as unknown as WS,
    (_value) => { throw new Error("Not wrapped by provider") },
  ])
  const Provider = createProvider(reactContext, stateContext, createWorker)
  return {
    reactContext,
    stateContext,
    Provider,
  }
}
