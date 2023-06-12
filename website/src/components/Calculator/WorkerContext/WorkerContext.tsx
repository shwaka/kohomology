import React, { Context, createContext, ReactNode, useRef, useState } from "react"
import { WorkerWrapper } from "./WorkerWrapper"

type OmitIfEmpty<T, K extends string | number | symbol> =
  T extends { [_ in K]: infer S}
    ? (Record<string, never> extends S ? Omit<T, K> : T)
    : T

interface ProviderProps<WO, WS> {
  createWorker: () => Worker
  defaultState: WS
  children: ReactNode
}

type StateAndSetState<WS> = [
  WS,
  React.Dispatch<React.SetStateAction<WS>>,
]

type StateContext<WS> = Context<StateAndSetState<WS>>

export type WorkerContext<WI, WO, WS> = {
  reactContext: Context<WorkerWrapper<WI, WO, WS>>
  stateContext: StateContext<WS>
  Provider: (props: OmitIfEmpty<ProviderProps<WO, WS>, "defaultState">) => JSX.Element
}

function WorkerContextProvider<WI, WO, WS>(
  props: {
    context: Context<WorkerWrapper<WI, WO, WS>>
    stateContext: StateContext<WS>
  } & ProviderProps<WO, WS>
): JSX.Element {
  const wrapperRef = useRef<WorkerWrapper<WI, WO, WS> | null>(null)
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

function createProvider<WI, WO, WS>(
  reactContext: Context<WorkerWrapper<WI, WO, WS>>,
  stateContext: StateContext<WS>,
): ((props: OmitIfEmpty<ProviderProps<WO, WS>, "defaultState">) => JSX.Element) {
  const WorkerContextProviderCurried = (props: OmitIfEmpty<ProviderProps<WO, WS>, "defaultState">): JSX.Element =>  {
    // If props does not contain defaultState, then WS is empty.
    const defaultState: WS =
      "defaultState" in props ? props.defaultState : ({} as WS)
    return (
      <WorkerContextProvider
        context={reactContext}
        stateContext={stateContext}
        createWorker={props.createWorker}
        defaultState={defaultState}
      >
        {props.children}
      </WorkerContextProvider>
    )
  }
  return WorkerContextProviderCurried
}

export function createWorkerContext<WI, WO, WS>(): WorkerContext<WI, WO, WS> {
  const reactContext = createContext<WorkerWrapper<WI, WO, WS>>(WorkerWrapper.default())
  const stateContext = createContext<StateAndSetState<WS>>([
    undefined as unknown as WS,
    (_value) => { throw new Error("Not wrapped by provider") },
  ])
  const Provider = createProvider(reactContext, stateContext)
  return {
    reactContext,
    stateContext,
    Provider,
  }
}
