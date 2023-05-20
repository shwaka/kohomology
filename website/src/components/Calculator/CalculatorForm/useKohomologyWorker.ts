import { useCallback, useEffect } from "react"
import { useWorker } from "../WorkerContext"
import { kohomologyWorkerContext } from "../kohomologyWorkerContext"
import { StyledMessage } from "../styled/message"
import { WorkerInfo, WorkerInput, WorkerOutput } from "../worker/workerInterface"

interface UseKohomologyWorkerArgs {
  defaultJson: string
  onmessage: (output: WorkerOutput) => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  dgaInfo: StyledMessage[]
  workerInfo: WorkerInfo
  postMessage: (input: WorkerInput) => void
  restart: () => void
}

export function useKohomologyWorker({
  defaultJson, onmessage
}: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  // const [worker, setWorker] = useState(() => new KohomologyWorker())

  const { postMessage, addListener, addRestartListener, restart, state: { json, dgaInfo, workerInfo } } = useWorker(kohomologyWorkerContext)

  const setJson = useCallback((newJson: string): void => {
    const inputUpdate: WorkerInput = {
      command: "updateJson",
      json: newJson,
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
  }, [setJson, defaultJson])

  // worker.onmessage = onmessage
  // const postMessage = worker.postMessage.bind(worker)

  return { json, setJson, dgaInfo, workerInfo, postMessage, restart }
}
