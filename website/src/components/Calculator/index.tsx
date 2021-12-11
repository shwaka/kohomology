import React, { FormEvent, useState } from "react"
import { FreeDGAWrapper } from "kohomology-js"
import "katex/dist/katex.min.css"
import styles from "./styles.module.scss"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { StyledMessage, toStyledMessage } from "./styled"

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface CalculatorFormProps {
  printResult: (result: StyledMessage[]) => void
  printError: (errorString: string) => void
  dgaWrapper: FreeDGAWrapper
  setDgaWrapper: (dgaWrapper: FreeDGAWrapper) => void
}

function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [json, setJson] = useState(sphere(2))
  const [maxDegree, setMaxDegree] = useState("20")
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
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
    try {
      props.printResult(props.dgaWrapper.computeCohomologyUpTo(parseInt(maxDegree)).map(toStyledMessage))
    } catch (error: unknown) {
      printError(error)
    }
  }
  function handleFreeLoopSpaceButton(e: FormEvent): void {
    e.preventDefault()
    try {
      props.printResult(props.dgaWrapper.computeCohomologyOfFreeLoopSpaceUpTo(parseInt(maxDegree)).map(toStyledMessage))
    } catch (error: unknown) {
      printError(error)
    }
  }
  function applyJson(e: FormEvent): void {
    e.preventDefault()
    try {
      props.setDgaWrapper(new FreeDGAWrapper(json))
    } catch (error: unknown) {
      printError(error)
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
      <form onSubmit={(e) => e.preventDefault()}>
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
          <input type="button" value="Compute" onClick={handleCohomologyButton} />
          <input type="button" value="Compute free loop space" onClick={handleFreeLoopSpaceButton} />
        </div>
      </form>
    </div>
  )
}

export function Calculator(): JSX.Element {
  const initialMessage = StyledMessage.fromString("success", "Computation results will be shown here")
  const [messages, setMessages] = useState<StyledMessage[]>([initialMessage])
  const [dgaWrapper, setDgaWrapper] = useState(new FreeDGAWrapper(sphere(2)))
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
        dgaWrapper={dgaWrapper}
        setDgaWrapper={setDgaWrapper}
      />
      <div>
        <div>{toStyledMessage(dgaWrapper.dgaInfo()).toJSXElement()}</div>
        <div className={styles.calculatorResults}>
          {messages.map((message, index) => message.toJSXElement(index))}
        </div>
      </div>
    </div>
  )
}
