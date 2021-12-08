import React, { FormEvent, useState } from "react"
import { computeCohomology as computeCohomologyKotlin, Text as TextKotlin } from "kohomology-js"
import "katex/dist/katex.min.css"
import TeX from "@matejmazur/react-katex"
import styles from "./styles.module.css"
import { sphere, complexProjective, sevenManifold } from "./examples"

interface Text {
  type: "normal" | "math" | "invalid"
  content: string
}

function convertToText(textKotlin: TextKotlin): Text {
  if (["normal", "math"].includes(textKotlin.type)) {
    return textKotlin as Text
  } else {
    return { type: "invalid", content: textKotlin.content }
  }
}

function computeCohomology(json: string, maxDegree: number): Text[] {
  return computeCohomologyKotlin(json, maxDegree).map((textKotlin) => convertToText(textKotlin))
}

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface CalculatorFormProps {
  printResult: (result: Text[]) => void
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
  const [error, setError] = useState<string | null>(null)
  // const [equations, setEquations] = useState<string[]>([])
  const [texts, setTexts] = useState<Text[]>([])
  return (
    <div>
      <CalculatorForm
        printResult={(result: Text[]) => {setTexts(result); setError(null)}}
        printError={(errorString: string) => {setTexts([]); setError(errorString)}}
      />
      <div>
        {error && <div className={styles.error}>{error}</div>}
        {texts.map((text, index) => {
          switch (text.type) {
            case "normal":
              return <div key={index}>{text.content}</div>
            case "math":
              return <div key={index}><TeX math={text.content}/></div>
            case "invalid":
              return <div className={styles.error}>{text.content}</div>
          }
        })}
      </div>
    </div>
  )
}
