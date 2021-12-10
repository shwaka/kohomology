import React, { FormEvent, useState } from "react"
import { computeCohomology as computeCohomologyKt } from "kohomology-js"
import "katex/dist/katex.min.css"
import styles from "./styles.module.css"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { StyledMessage, toStyledMessage } from "./styled"

function computeCohomology(json: string, maxDegree: number): StyledMessage[] {
  return computeCohomologyKt(json, maxDegree).map(toStyledMessage)
}

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface CalculatorFormProps {
  printResult: (result: StyledMessage[]) => void
  printError: (errorString: string) => void
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
  function handleSubmit(e: FormEvent): void {
    e.preventDefault()
    try {
      props.printResult(computeCohomology(json, parseInt(maxDegree)))
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
    <div>
      {createButton("S^2", sphere(2))}
      {createButton("CP^3", complexProjective(3))}
      {createButton("7-mfd", sevenManifold())}
      <form onSubmit={handleSubmit}>
        <div>
          <span>max degree</span>
          <input type="number" value={maxDegree} onChange={handleChangeMaxDegree} />
        </div>
        <textarea rows={20} cols={80}
          value={json} onChange={handleChangeJson} />
        <div>
          <input type="button" value="Compute" onClick={handleSubmit} />
        </div>
      </form>
    </div>
  )
}

export function Calculator(): JSX.Element {
  const [messages, setMessages] = useState<StyledMessage[]>([])
  function addMessages(addedMessages: StyledMessage[]): void {
    setMessages(messages.concat(addedMessages))
  }
  return (
    <div>
      <CalculatorForm
        printResult={addMessages}
        printError={(errorString: string) => {
          addMessages([StyledMessage.fromString("error", errorString)])
        }}
      />
      <div>
        {messages.map((message, index) => message.toJSXElement(index))}
      </div>
    </div>
  )
}
