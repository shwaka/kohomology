import React, { FormEvent, useState } from "react"
import { computeCohomology } from "kohomology-js"

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

export function Calculator(): JSX.Element {
  const [json, setJson] = useState(sphere(2))
  const [maxDegree, setMaxDegree] = useState("20")
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    console.log(computeCohomology(json, parseInt(maxDegree)))
  }
  function handleChangeMaxDegree(e: InputEvent) {
    setMaxDegree(e.target.value)
  }
  function handleChangeJson(e: TextAreaEvent) {
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
