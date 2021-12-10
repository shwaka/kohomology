import React, { FormEvent, useState } from "react"
import { FreeDGAWrapper, StyledStringKt } from "kohomology-js"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { StyledMessage, toStyledMessage } from "./styled"

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface CalculatorFormProps {
  printResult: (result: StyledMessage[]) => void
  printError: (errorString: string) => void
  setInfo: (infoElm: JSX.Element) => void
}

function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [json, setJson] = useState(sphere(2))
  const [maxDegree, setMaxDegree] = useState("20")
  const [dgaWrapper, setDgaWrapper] = useState(new FreeDGAWrapper("[]"))
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
  function handleSubmit(e: FormEvent): void {
    e.preventDefault()
    try {
      props.printResult(dgaWrapper.computeCohomologyUpTo(parseInt(maxDegree)).map(toStyledMessage))
    } catch (error: unknown) {
      if (error === null) {
        props.printError("This can't happen!")
      } else if (typeof error === "object") {
        props.printError(error.toString())
      } else {
        props.printError("Unknown error!")
      }
    }
  }
  function applyJson(e: FormEvent): void {
    e.preventDefault()
    try {
      const newDgaWrapper = new FreeDGAWrapper(json)
      setDgaWrapper(newDgaWrapper)
      props.setInfo(toStyledMessage(newDgaWrapper.dgaInfo()).toJSXElement())
    } catch (error: unknown) {
      if (error === null) {
        props.printError("This can't happen!")
      } else if (typeof error === "object") {
        props.printError(error.toString())
      } else {
        props.printError("Unknown error!")
      }
    }
  }
  function handleChangeMaxDegree(e: InputEvent): void {
    setMaxDegree(e.target.value)
  }
  function handleChangeJson(e: TextAreaEvent): void {
    setJson(e.target.value)
  }
  return (
    <div className={styles.calculatorForm}>
      {createButton("S^2", sphere(2))}
      {createButton("CP^3", complexProjective(3))}
      {createButton("7-mfd", sevenManifold())}
      <form onSubmit={handleSubmit}>
        <div>
          <span>max degree</span>
          <input type="number" value={maxDegree} onChange={handleChangeMaxDegree} />
        </div>
        <textarea
          value={json} onChange={handleChangeJson} />
        <div>
          <input type="button" value="Apply" onClick={applyJson} />
        </div>
        <div>
          <input type="button" value="Compute" onClick={handleSubmit} />
        </div>
      </form>
    </div>
  )
}

export function Calculator(): JSX.Element {
  const initialMessage = StyledMessage.fromString("success", "Computation results will be shown here")
  const [messages, setMessages] = useState<StyledMessage[]>([initialMessage])
  const [info, setInfo] = useState<JSX.Element>(<span>info</span>)
  function addMessages(addedMessages: StyledMessage[]): void {
    setMessages(messages.concat(addedMessages))
  }
  return (
    <div className={styles.calculator}>
      <CalculatorForm
        printResult={addMessages}
        printError={(errorString: string) => {
          addMessages([StyledMessage.fromString("error", errorString)])
        }}
        setInfo={setInfo}
      />
      <div>
        <div>{info}</div>
        <div className={styles.calculatorResults}>
          {messages.map((message, index) => message.toJSXElement(index))}
        </div>
      </div>
    </div>
  )
}
