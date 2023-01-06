import { useCallback, useEffect, useRef, useState } from "react"
import KohomologyWorker from "worker-loader!../worker/kohomology.worker"
import { WorkerInput, WorkerOutput } from "../worker/workerInterface"

interface UseKohomologyWorkerArgs {
  defaultJson: string
  onmessage: (e: MessageEvent<WorkerOutput>) => void
}

interface UseKohomologyWorkerResult {
  json: string
  setJson: (json: string) => void
  worker: KohomologyWorker
}

export function useKohomologyWorker({ defaultJson, onmessage }: UseKohomologyWorkerArgs): UseKohomologyWorkerResult {
  const [json, setJson] = useState(defaultJson)

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  const workerRef = useRef(new KohomologyWorker())
  const worker: KohomologyWorker = workerRef.current

  worker.onmessage = onmessage

  // Update worker when json is changed
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

  return { json, setJson, worker }
}
