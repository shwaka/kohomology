import { useCallback, useEffect } from "react"

import { StyledMessage } from "@calculator/styled/message"
import { useWorker } from "@calculator/WorkerContext"
import { RunAsync } from "@calculator/WorkerContext/useWorker"

import { kohomologyWorkerContext } from "../kohomologyWorker/kohomologyWorkerContext"
import { KohomologyWorkerFunc, WorkerInfo, KohomologyWorkerInput, KohomologyWorkerOutput } from "../kohomologyWorker/workerInterface"

interface UseKohomologyWorkerArgs {
  defaultJson: string
  defaultIdealJson: string
  onmessage: (output: KohomologyWorkerOutput) => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  idealJson: string
  setIdealJson: (idealJson: string) => void
  dgaInfo: StyledMessage[]
  idealInfo: StyledMessage
  workerInfo: WorkerInfo
  postMessage: (input: KohomologyWorkerInput) => void
  restart: () => void
  runAsync: RunAsync<KohomologyWorkerFunc>
}

export function useKohomologyWorker({
  defaultJson, defaultIdealJson, onmessage
}: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {
  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  // const [worker, setWorker] = useState(() => new KohomologyWorker())

  const { postMessage, addListener, addRestartListener, restart, state: { json, dgaInfo, idealJson, idealInfo, workerInfo }, runAsync } = useWorker(kohomologyWorkerContext)

  const setJson = useCallback((newJson: string): void => {
    const inputUpdate: KohomologyWorkerInput = {
      command: "updateJson",
      json: newJson,
    }
    postMessage(inputUpdate)
  }, [postMessage])

  const setIdealJson = useCallback((newIdealJson: string): void => {
    const inputUpdate: KohomologyWorkerInput = {
      command: "updateIdealJson",
      idealJson: newIdealJson,
    }
    postMessage(inputUpdate)
  }, [postMessage])

  useEffect(() => {
    addListener("useKohomologyWorker", onmessage)
  }, [addListener, onmessage])

  useEffect(() => {
    addRestartListener("useKohomologyWorker", () => {
      // When the worker is restarted, json should be set again
      // (c.f. see the initialization below)
      setJson(json)
    })
  }, [addRestartListener, setJson, json])

  // initialization
  useEffect(() => {
    setJson(defaultJson)
    setIdealJson(defaultIdealJson)
  }, [setJson, defaultJson, setIdealJson, defaultIdealJson])

  // worker.onmessage = onmessage
  // const postMessage = worker.postMessage.bind(worker)

  return { json, setJson, idealJson, setIdealJson, dgaInfo, idealInfo, workerInfo, postMessage, restart, runAsync }
}
