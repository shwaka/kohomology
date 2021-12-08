import React, { FormEvent, useState } from "react"
import { computeCohomology as computeCohomologyKotlin, Text as TextKotlin } from "kohomology-js"
import "katex/dist/katex.min.css"
import TeX from "@matejmazur/react-katex"
import styles from "./styles.module.css"

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

function sphere(dim: number): string {
  if (!Number.isInteger(dim)) {
    throw new Error("dim must be an integer")
  }
  if (dim <= 0) {
    throw new Error("dim must be positive")
  }
  if (dim % 2 == 0) {
    return `[
  ["x", ${dim}, "zero"],
  ["y", ${2*dim - 1}, "x^2"]
]`
  } else {
    return `[
  ["x", ${dim}, "zero"]
]`
  }
}

function complexProjective(n: number): string {
  if (!Number.isInteger(n)) {
    throw new Error("dim must be an integer")
  }
  if (n <= 0) {
    throw new Error("dim must be positive")
  }
  return `[
  ["c", 2, "zero"],
  ["x", ${2*n + 1}, "c^${n + 1}"]
]`
}

function sevenManifold(): string {
  return `[
  ["a", 2, "zero"],
  ["b", 2, "zero"],
  ["x", 3, "a^2"],
  ["y", 3, "a*b"],
  ["z", 3, "b^2"]
]`
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
