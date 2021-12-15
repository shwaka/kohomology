import React, { FormEvent, useEffect, useRef, useState } from "react"
import TeX from "@matejmazur/react-katex"
import { FreeDGAWrapper } from "kohomology-js"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { fromString, StyledMessage, StyledString, toStyledMessage } from "./styled"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "./workerInterface"
import KohomologyWorker from "worker-loader!./kohomology.worker"

const worker = new KohomologyWorker()

function styledStringtoJSXElement(styledString: StyledString, key: number): JSX.Element {
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

function styledMessagetoJSXElement(styledMessage: StyledMessage, key: number = 0): JSX.Element {
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
      {styledMessage.strings.map((styledString, index) => styledStringtoJSXElement(styledString, index))}
    </div>
  )
}

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface JsonEditorProps {
  json: string
  updateDgaWrapper: (json: string) => void
  finish: () => void
}

function JsonEditor(props: JsonEditorProps): JSX.Element {
  const [json, setJson] = useState(props.json)
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input
        type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
  function handleChangeJson(e: TextAreaEvent): void {
    setJson(e.target.value)
  }
  return (
    <div className={styles.jsonEditor}>
      {createButton("S^2", sphere(2))}
      {createButton("CP^3", complexProjective(3))}
      {createButton("7-mfd", sevenManifold())}
      <textarea
        value={json} onChange={handleChangeJson} />
      <input
        type="button" value="Apply"
        onClick={() => { props.updateDgaWrapper(json); props.finish() }} />
      <input
        type="button" value="Cancel"
        onClick={() => { props.finish() }} />
    </div>
  )
}

interface CalculatorFormProps {
  printResult: (result: StyledMessage | StyledMessage[]) => void
  printError: (errorString: string) => void
}

function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const [json, setJson] = useState(sphere(2))
  // const [dgaWrapper, setDgaWrapper] = useState(new FreeDGAWrapper(sphere(2)))
  const [editingJson, setEditingJson] = useState(false)
  const [targetName, setTargetName] = useState<TargetName>("self")
  function printError(error: unknown): void {
    if (error === null) {
      props.printError("This can't happen!")
    } else if (typeof error === "object") {
      props.printError(error.toString())
    } else {
      props.printError("Unknown error!")
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
    setJson(json)
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
  function handleChangeMaxDegree(e: InputEvent): void {
    setMaxDegree(e.target.value)
  }
  return (
    <div className={styles.calculatorForm}>
      <input type="button" value="Edit DGA" onClick={() => setEditingJson(true)} />
      {editingJson &&
       <JsonEditor
         json={json} updateDgaWrapper={applyJson}
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

export function Calculator(): JSX.Element {
  const initialMessage = fromString("success", "Computation results will be shown here")
  const [messages, setMessages] = useState<StyledMessage[]>([initialMessage])
  const scrollRef = useRef<HTMLDivElement>(null)

  function addMessages(addedMessages: StyledMessage | StyledMessage[]): void {
    if (addedMessages instanceof Array) {
      setMessages((prevMessages) => prevMessages.concat(addedMessages))
    } else {
      setMessages((prevMessages) => prevMessages.concat([addedMessages]))
    }
  }

  worker.onmessage = (e: MessageEvent<WorkerOutput>) => {
    addMessages(e.data.messages)
  }

  function scrollToBottom(): void {
    const div: HTMLDivElement | null = scrollRef.current
    if (div !== null) {
      setTimeout(() => {
        div.scrollTo({ top: div.scrollHeight, behavior: "smooth" })
      })
    }
  }
  useEffect(() => { scrollToBottom() }, [messages])
  return (
    <div className={styles.calculator}>
      <CalculatorForm
        printResult={addMessages}
        printError={(errorString: string) => {
          addMessages([fromString("error", errorString)])
        }}
      />
      <div className={styles.calculatorResults} ref={scrollRef}>
        {messages.map((message, index) => styledMessagetoJSXElement(message, index))}
      </div>
    </div>
  )
}
