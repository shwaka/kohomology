import { useCallback, useEffect } from "react"
import { useWorker } from "../WorkerContext"
import { kohomologyWorkerContext } from "../kohomologyWorkerContext"
import { StyledMessage } from "../styled/message"
import { WorkerInput, WorkerOutput } from "../worker/workerInterface"

interface UseKohomologyWorkerArgs {
  defaultJson: string
  onmessage: (output: WorkerOutput) => void
  resetWorkerInfo: () => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  dgaInfo: StyledMessage[]
  postMessage: (input: WorkerInput) => void
  restart: () => void
}

export function useKohomologyWorker({
  defaultJson, onmessage, resetWorkerInfo
}: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  // const [worker, setWorker] = useState(() => new KohomologyWorker())

  const { postMessage, addListener, restart, addRestartListener, state: { json, dgaInfo } } = useWorker(kohomologyWorkerContext)

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
      resetWorkerInfo()
    })
  }, [addRestartListener, resetWorkerInfo])

  // initialization
  useEffect(() => {
    setJson(defaultJson)
  }, [setJson, defaultJson])

  // worker.onmessage = onmessage
  // const postMessage = worker.postMessage.bind(worker)

  return { json, setJson, dgaInfo, postMessage, restart }
}
