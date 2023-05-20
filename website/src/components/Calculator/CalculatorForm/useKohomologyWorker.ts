import { useCallback, useEffect, useState } from "react"
import { useWorker } from "../WorkerContext"
import { kohomologyWorkerContext } from "../kohomologyWorkerContext"
import { WorkerInput, WorkerOutput } from "../worker/workerInterface"

interface UseKohomologyWorkerArgs {
  onmessage: (output: WorkerOutput) => void
  resetWorkerInfo: () => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  postMessage: (input: WorkerInput) => void
  restart: () => void
}

export function useKohomologyWorker({
  onmessage, resetWorkerInfo
}: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  // const [worker, setWorker] = useState(() => new KohomologyWorker())

  const { postMessage, addListener, restart, addRestartListener, state: { json } } = useWorker(kohomologyWorkerContext)

  const showDgaInfo = useCallback((): void => {
    const inputShowInfo: WorkerInput = {
      command: "dgaInfo"
    }
    postMessage(inputShowInfo)
  }, [postMessage])

  const setJson = useCallback((newJson: string): void => {
    const inputUpdate: WorkerInput = {
      command: "updateJson",
      json: newJson,
    }
    postMessage(inputUpdate)
    showDgaInfo()
  }, [postMessage, showDgaInfo])

  const updateJson = useCallback((): void => {
    // setJson(json)
    const inputUpdate: WorkerInput = {
      command: "updateJson",
      json: json,
    }
    postMessage(inputUpdate)
    const inputShowInfo: WorkerInput = {
      command: "dgaInfo"
    }
    postMessage(inputShowInfo)
  }, [json, postMessage])

  useEffect(() => {
    addListener("useKohomologyWorker", onmessage)
  }, [addListener, onmessage])

  useEffect(() => {
    addRestartListener("useKohomologyWorker", () => {
      resetWorkerInfo()
      updateJson()
    })
  }, [addRestartListener, resetWorkerInfo, updateJson])

  // worker.onmessage = onmessage
  // const postMessage = worker.postMessage.bind(worker)

  // KohomologyWorker (kohomology-js) also stores json (as a FreeDGAlgebra defined from it)
  // to cache computation results.
  // This violates the principle "single source of truth",
  // but such implementation seems to be efficient since KohomologyWorker is run in a different thread.
  // Hence it is necessary to update worker when
  // - json is changed or
  // - worker is restarted.
  useEffect(() => {
    updateJson()
  }, [updateJson])

  return { json, setJson, postMessage, restart }
}
