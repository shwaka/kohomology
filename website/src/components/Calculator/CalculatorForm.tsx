import React, { FormEvent, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { sphere } from "./examples"
import { fromString, StyledMessage } from "./styled"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "./workerInterface"
import { JsonEditor } from "./JsonEditor"
import KohomologyWorker from "worker-loader!./kohomology.worker"

const worker = new KohomologyWorker()

type InputEvent = React.ChangeEvent<HTMLInputElement>

interface CalculatorFormProps {
  printMessages: (result: StyledMessage | StyledMessage[]) => void
}

export function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const [json, setJson] = useState(sphere(2))
  // const [dgaWrapper, setDgaWrapper] = useState(new FreeDGAWrapper(sphere(2)))
  const [editingJson, setEditingJson] = useState(false)
  const [targetName, setTargetName] = useState<TargetName>("self")


  worker.onmessage = (e: MessageEvent<WorkerOutput>) => {
    props.printMessages(e.data.messages)
  }

  function printError(error: unknown): void {
    if (error === null) {
      props.printMessages(fromString("error", "This can't happen!"))
    } else if (typeof error === "object") {
      props.printMessages(fromString("error", error.toString()))
    } else {
      props.printMessages(fromString("error", "Unknown error!"))
    }
  }
  function handleCohomologyButton(e: FormEvent): void {
    e.preventDefault()
    const input: WorkerInput = {
      command: "computeCohomology",
      targetName: targetName,
      maxDegree: parseInt(maxDegree),
    }
    worker.postMessage(input)
    // props.printResult(toStyledMessage(dgaWrapper.computationHeader(targetName)))
    // const compute = (degree: number, maxDegree: number): void => {
    //   setTimeout(() => {
    //     props.printResult(toStyledMessage(dgaWrapper.computeCohomology(targetName, degree)))
    //     if (degree < maxDegree) {
    //       compute(degree + 1, maxDegree)
    //     }
    //   })
    // }
    // compute(0, parseInt(maxDegree))
  }

  function applyJson(json: string): void {
    // setJson(json)
    const input: WorkerInput = {
      command: "updateJson",
      json: json,
    }
    worker.postMessage(input)
    // try {
    //   setDgaWrapper(new FreeDGAWrapper(json))
    // } catch (error: unknown) {
    //   printError(error)
    // }
  }

  useEffect(() => {
    applyJson(json)
  }, [json])

  function handleChangeMaxDegree(e: InputEvent): void {
    setMaxDegree(e.target.value)
  }
  return (
    <div className={styles.calculatorForm}>
      <input type="button" value="Edit DGA" onClick={() => setEditingJson(true)} />
      {editingJson &&
       <JsonEditor
         json={json} updateDgaWrapper={setJson}
         finish={() => setEditingJson(false)}
       />
      }
      <div>
        {targetNames.map((targetNameForLabel, index) =>
          <label key={index}>
            <input
              type="radio" name="targetName"
              value={targetNameForLabel} checked={targetNameForLabel === targetName}
              onChange={() => setTargetName(targetNameForLabel)} />
            {targetNameForLabel}
          </label>
        )}
      </div>
      <div className={styles.computeCohomology}>
        <input type="button" value="Compute cohomology" onClick={handleCohomologyButton} />
        <span>up to degree</span>
        <input
          type="number" value={maxDegree} onChange={handleChangeMaxDegree}
          min={0} className={styles.maxDegree} />
      </div>
    </div>
  )
}
