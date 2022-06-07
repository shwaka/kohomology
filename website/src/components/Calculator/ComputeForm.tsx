import React, { FormEvent } from "react"
import styles from "./styles.module.scss"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

export interface ComputeFormProps {
  handleCohomologyButton: (e: FormEvent) => void
  handleComputeCohomologyClassButton: (e: FormEvent) => void
  maxDegree: string
  handleChangeMaxDegree: (e: InputEvent) => void
  cocycleString: string
  setCocycleString: (cocycle: string) => void
}

export function ComputeForm({ handleCohomologyButton, handleComputeCohomologyClassButton, maxDegree, handleChangeMaxDegree, cocycleString, setCocycleString }: ComputeFormProps): JSX.Element {
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
