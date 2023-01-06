import { useCallback, useEffect, useState } from "react"
import KohomologyWorker from "worker-loader!../worker/kohomology.worker"
import { WorkerInput, WorkerOutput } from "../worker/workerInterface"

interface UseKohomologyWorkerArgs {
  defaultJson: string
  onmessage: (e: MessageEvent<WorkerOutput>) => void
  resetWorkerInfo: () => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  postMessage: (input: WorkerInput) => void
  restart: () => void
}

export function useKohomologyWorker({
  defaultJson, onmessage, resetWorkerInfo
}: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {
  const [json, setJson] = useState(defaultJson)

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  const [worker, setWorker] = useState(() => new KohomologyWorker())

  worker.onmessage = onmessage
  const postMessage = worker.postMessage.bind(worker)

  // KohomologyWorker (kohomology-js) also stores json (as a FreeDGAlgebra defined from it)
  // to cache computation results.
  // This violates the principle "single source of truth",
  // but such implementation seems to be efficient since KohomologyWorker is run in a different thread.
  // Hence it is necessary to update worker when
  // - json is changed or
  // - worker is restarted.
  useEffect(() => {
    // setJson(json)
    const inputUpdate: WorkerInput = {
      command: "updateJson",
      json: json,
    }
    worker.postMessage(inputUpdate)
    const inputShowInfo: WorkerInput = {
      command: "dgaInfo"
    }
    worker.postMessage(inputShowInfo)
  }, [json, worker])

  const restart = useCallback(
    () => {
      worker.terminate()
      setWorker(new KohomologyWorker())
      resetWorkerInfo()
    },
    [worker, setWorker, resetWorkerInfo]
  )

  return { json, setJson, postMessage, restart }
}
