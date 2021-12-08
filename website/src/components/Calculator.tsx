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

type InputEvent = React.ChangeEvent<HTMLInputElement>
type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

export function Calculator(): JSX.Element {
  const [json, setJson] = useState(sphere(2))
  const [maxDegree, setMaxDegree] = useState("20")
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
