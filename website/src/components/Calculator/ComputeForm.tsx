import React, { FormEvent, useCallback, useState } from "react"
import KohomologyWorker from "worker-loader!./kohomology.worker"
import styles from "./styles.module.scss"
import { TargetName, WorkerInput } from "./workerInterface"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

export interface ComputeFormProps {
  targetName: TargetName
  worker: KohomologyWorker
}

export function ComputeForm({ targetName, worker }: ComputeFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const [cocycleString, setCocycleString] = useState("x^2")

  const handleChangeMaxDegree = useCallback(
    (e: InputEvent): void => {
      setMaxDegree(e.target.value)
    },
    []
  )

  const handleCohomologyButton = useCallback(
    (e: FormEvent): void => {
      e.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomology",
        targetName: targetName,
        maxDegree: parseInt(maxDegree),
      }
      worker.postMessage(input)
    },
    [targetName, maxDegree, worker]
  )
  const handleComputeCohomologyClassButton = useCallback(
    (e: FormEvent): void => {
      e.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomologyClass",
        targetName: targetName,
        cocycleString: cocycleString,
      }
      worker.postMessage(input)
    },
    [targetName, cocycleString, worker]
  )

  return (
    <React.Fragment>
      <form className={styles.computeCohomology} onSubmit={handleCohomologyButton}>
        <input type="submit" value="Compute cohomology"/>
        <span>up to degree</span>
        <input
          type="number" value={maxDegree} onChange={handleChangeMaxDegree}
          min={0} className={styles.maxDegree} />
      </form>
      <form className={styles.computeCohomology} onSubmit={handleComputeCohomologyClassButton}>
        <input type="submit" value="Compute class" />
        <span>cocycle:</span>
        <input
          type="text" value={cocycleString} onChange={(e) => setCocycleString(e.target.value)}
          onSubmit={handleComputeCohomologyClassButton} />
      </form>
    </React.Fragment>
  )
}
