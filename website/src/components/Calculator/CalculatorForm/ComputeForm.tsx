import React, { FormEvent, useCallback, useState } from "react"
import { TargetName, WorkerInput } from "../worker/workerInterface"
import styles from "./styles.module.scss"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

export interface ComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: WorkerInput) => void
}

function ComputeCohomologyForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const handleCohomologyButton = useCallback(
    (e: FormEvent): void => {
      e.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomology",
        targetName: targetName,
        maxDegree: parseInt(maxDegree),
      }
      postMessageToWorker(input)
    },
    [targetName, maxDegree, postMessageToWorker]
  )
  return (
    <form className={styles.computeCohomology} onSubmit={handleCohomologyButton}>
      <input type="submit" value="Compute cohomology"/>
      <span>up to degree</span>
      <input
        type="number" value={maxDegree} onChange={(e) => setMaxDegree(e.target.value)}
        min={0} className={styles.maxDegree} />
    </form>
  )
}

function ComputeClassForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
  const [cocycleString, setCocycleString] = useState("x^2")
  const handleComputeCohomologyClassButton = useCallback(
    (e: FormEvent): void => {
      e.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomologyClass",
        targetName: targetName,
        cocycleString: cocycleString,
      }
      postMessageToWorker(input)
    },
    [targetName, cocycleString, postMessageToWorker]
  )
  return (
    <form className={styles.computeCohomology} onSubmit={handleComputeCohomologyClassButton}>
      <input type="submit" value="Compute class" />
      <span>cocycle:</span>
      <input
        type="text" value={cocycleString} onChange={(e) => setCocycleString(e.target.value)}
        onSubmit={handleComputeCohomologyClassButton} />
    </form>
  )
}

export function ComputeForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
  return (
    <React.Fragment>
      <ComputeCohomologyForm targetName={targetName} postMessageToWorker={postMessageToWorker}/>
      <ComputeClassForm targetName={targetName} postMessageToWorker={postMessageToWorker}/>
    </React.Fragment>
  )
}
