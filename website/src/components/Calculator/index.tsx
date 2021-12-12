import React, { FormEvent, useEffect, useRef, useState } from "react"
import { FreeDGAWrapper } from "kohomology-js"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { StyledMessage, toStyledMessage } from "./styled"

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
  printResult: (result: StyledMessage[]) => void
  printError: (errorString: string) => void
}

const targetNames = ["self", "freeLoopSpace"] as const
type TargetName = (typeof targetNames)[number]

function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [maxDegree, setMaxDegree] = useState("20")
  const [dgaWrapper, setDgaWrapper] = useState(new FreeDGAWrapper(sphere(2)))
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
    props.printResult([toStyledMessage(dgaWrapper.computationHeader(targetName))])
    const compute = (degree: number, maxDegree: number): void => {
      setTimeout(() => {
        props.printResult([toStyledMessage(dgaWrapper.computeCohomology(targetName, degree))])
        if (degree < maxDegree) {
          compute(degree + 1, maxDegree)
        }
      })
    }
    compute(0, parseInt(maxDegree))
  }

  function applyJson(json: string): void {
    try {
      setDgaWrapper(new FreeDGAWrapper(json))
    } catch (error: unknown) {
      printError(error)
    }
  }
  function handleChangeMaxDegree(e: InputEvent): void {
    setMaxDegree(e.target.value)
  }
  return (
    <div className={styles.calculatorForm}>
      <div>
        {dgaWrapper.dgaInfo().map(
          (styledMessageKt, index) => toStyledMessage(styledMessageKt).toJSXElement(index)
        )}
      </div>
      <input type="button" value="Edit DGA" onClick={() => setEditingJson(true)} />
      {editingJson &&
       <JsonEditor
         json={dgaWrapper.json} updateDgaWrapper={applyJson}
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
  const initialMessage = StyledMessage.fromString("success", "Computation results will be shown here")
  const [messages, setMessages] = useState<StyledMessage[]>([initialMessage])
  const scrollRef = useRef<HTMLDivElement>(null)
  function addMessages(addedMessages: StyledMessage[]): void {
    setMessages((prevMessages) => prevMessages.concat(addedMessages))
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
          addMessages([StyledMessage.fromString("error", errorString)])
        }}
      />
      <div className={styles.calculatorResults} ref={scrollRef}>
        {messages.map((message, index) => message.toJSXElement(index))}
      </div>
    </div>
  )
}
