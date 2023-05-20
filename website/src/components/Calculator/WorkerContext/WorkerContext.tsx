import React, { Context, createContext, ReactNode, useRef, useState } from "react"
import { StateFromOutput } from "./StateFromOutput"
import { WorkerWrapper } from "./WorkerWrapper"

type OmitIfEmpty<T, K extends string | number | symbol> =
  T extends { [_ in K]: infer S}
    ? (Record<string, never> extends S ? Omit<T, K> : T)
    : T

interface ProviderProps<WI, WO> {
  createWorker: () => Worker
  defaultState: StateFromOutput<WO>
  children: ReactNode
}

type StateAndSetState<WI, WO> = [
  StateFromOutput<WO>,
  React.Dispatch<React.SetStateAction<StateFromOutput<WO>>>,
]

type StateContext<WI, WO> = Context<StateAndSetState<WI, WO>>

export type WorkerContext<WI, WO> = {
  reactContext: Context<WorkerWrapper<WI, WO>>
  stateContext: StateContext<WI, WO>
  Provider: (props: OmitIfEmpty<ProviderProps<WI, WO>, "defaultState">) => JSX.Element
}

function WorkerContextProvider<WI, WO>(
  props: {
    context: Context<WorkerWrapper<WI, WO>>
    stateContext: StateContext<WI, WO>
  } & ProviderProps<WI, WO>
): JSX.Element {
  const wrapperRef = useRef<WorkerWrapper<WI, WO> | null>(null)
  if (wrapperRef.current === null) {
    wrapperRef.current = new WorkerWrapper(props.createWorker)
  }
  const stateAndSetState = useState<StateFromOutput<WO>>(props.defaultState)

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

function createProvider<WI, WO>(
  reactContext: Context<WorkerWrapper<WI, WO>>,
  stateContext: StateContext<WI, WO>,
): ((props: OmitIfEmpty<ProviderProps<WI, WO>, "defaultState">) => JSX.Element) {
  const WorkerContextProviderCurried = (props: OmitIfEmpty<ProviderProps<WI, WO>, "defaultState">): JSX.Element =>  {
    // If props does not contain defaultState, then StateFromOutput<WO> is empty.
    const defaultState: StateFromOutput<WO> =
      "defaultState" in props ? props.defaultState : ({} as StateFromOutput<WO>)
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

export function createWorkerContext<WI, WO>(): WorkerContext<WI, WO> {
  const reactContext = createContext<WorkerWrapper<WI, WO>>(WorkerWrapper.default())
  const stateContext = createContext<StateAndSetState<WI, WO>>([
    undefined as unknown as StateFromOutput<WO>,
    (_value) => { throw new Error("Not wrapped by provider") },
  ])
  const Provider = createProvider(reactContext, stateContext)
  return {
    reactContext,
    stateContext,
    Provider,
  }
}
