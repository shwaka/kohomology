import React, { Context, createContext, ReactNode, useRef } from "react"
import { WorkerWrapper } from "./WorkerWrapper"

interface ProviderProps {
  createWorker: () => Worker
  children: ReactNode
}

export type WorkerContext<WI, WO> = {
  reactContext: Context<WorkerWrapper<WI, WO>>
  Provider: (props: ProviderProps) => JSX.Element
}

function WorkerContextProvider<WI, WO>(props: {
  context: Context<WorkerWrapper<WI, WO>>
  createWorker: () => Worker
  children: ReactNode
}): JSX.Element {
  const wrapperRef = useRef<WorkerWrapper<WI, WO> | null>(null)
  if (wrapperRef.current === null) {
    wrapperRef.current = new WorkerWrapper(props.createWorker)
  }

  const CurrentContext = props.context
  return (
    <CurrentContext.Provider value={wrapperRef.current}>
      {props.children}
    </CurrentContext.Provider>
  )
}

function createProvider<WI, WO>(
  reactContext: Context<WorkerWrapper<WI, WO>>
): ((props: ProviderProps) => JSX.Element) {
  const WorkerContextProviderCurried = (props: ProviderProps): JSX.Element =>  (
    <WorkerContextProvider
      context={reactContext}
      createWorker={props.createWorker}
    >
      {props.children}
    </WorkerContextProvider>
  )
  return WorkerContextProviderCurried
}

export function createWorkerContext<WI, WO>(): WorkerContext<WI, WO> {
  const reactContext = createContext<WorkerWrapper<WI, WO>>(WorkerWrapper.default())
  const Provider = createProvider(reactContext)
  return {
    reactContext,
    Provider,
  }
}
