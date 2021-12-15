import TeX from "@matejmazur/react-katex"
import React, { FormEvent, useEffect, useState } from "react"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!./kohomology.worker"
import { JsonEditor } from "./JsonEditor"
import { sphere } from "./examples"
import { fromString, StyledMessage, StyledString } from "./styled"
import styles from "./styles.module.scss"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "./workerInterface"

const worker = new KohomologyWorker()

function styledStringToJSXElement(styledString: StyledString, key: number): JSX.Element {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "normal":
      return <span key={key}>{styledString.content}</span>
    case "math":
      return <TeX key={key} math={styledString.content} settings={{ output: "html", macros: macros }} />
      // ↑{ output: "html" } is necessary to avoid strange behavior in 'overflow: scroll' (see memo.md for details)
  }
}

export function styledMessageToJSXElement(styledMessage: StyledMessage, key: number = 0): JSX.Element {
  let style: string
  switch (styledMessage.messageType) {
    case "success":
      style = styles.messageSuccess
      break
    case "error":
      style = styles.messageError
      break
  }
  return (
    <div key={key} className={style}>
      {styledMessage.strings.map((styledString, index) => styledStringToJSXElement(styledString, index))}
    </div>
  )
}

type InputEvent = React.ChangeEvent<HTMLInputElement>

interface CalculatorFormProps {
  printMessages: (result: StyledMessage | StyledMessage[]) => void
}

export function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const [json, setJson] = useState(sphere(2))
  const [editingJson, setEditingJson] = useState(false)
  const [targetName, setTargetName] = useState<TargetName>("self")
  const [dgaInfo, setDgaInfo] = useState<StyledMessage[]>([])

  worker.onmessage = (e: MessageEvent<WorkerOutput>) => {
    const output: WorkerOutput = e.data
    switch (output.command) {
      case "printMessages":
        props.printMessages(output.messages)
        break
      case "showDgaInfo":
        setDgaInfo(output.messages)
        break
    }
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
  }

  function applyJson(json: string): void {
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
  }

  useEffect(() => {
    applyJson(json)
  }, [json])

  function handleChangeMaxDegree(e: InputEvent): void {
    setMaxDegree(e.target.value)
  }
  return (
    <div className={styles.calculatorForm}>
      <div>
        {dgaInfo.map((styledMessage, index) => styledMessageToJSXElement(styledMessage, index))}
      </div>
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
