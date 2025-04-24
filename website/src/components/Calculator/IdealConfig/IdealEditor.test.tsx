import { render, renderHook, act, screen, fireEvent, waitFor } from "@testing-library/react"
import React from "react"
import { IdealEditor } from "./IdealEditor"
import { useIdealEditor, UseIdealEditorArgs } from "./useIdealEditor"

describe("IdealEditor", () => {
  test("empty text as generator", async () => {
    const hookArgs: UseIdealEditorArgs = {
      idealJson: "[]",
      setIdealJson: jest.fn(),
      validateGenerator: jest.fn().mockResolvedValue(true),
      validateGeneratorArray: jest.fn().mockResolvedValue(true),
    }
    const { result } = renderHook(() => useIdealEditor(hookArgs))
    const { rerender } = render(<IdealEditor {...result.current.idealEditorProps}/>)

    act(() => {
      result.current.idealEditorProps.append({ text: "" })
    })
    rerender(<IdealEditor {...result.current.idealEditorProps}/>)
    await act(async () => {
      await result.current.getOnSubmit(jest.fn())()
    })
    rerender(<IdealEditor {...result.current.idealEditorProps}/>)

    expect(screen.getByRole("alert")).toContainHTML("Please enter the generator.")
  })
})

function IdealEditorTestContainer(args: UseIdealEditorArgs): React.JSX.Element {
  const { idealEditorProps, getOnSubmit } = useIdealEditor(args)
  console.log(idealEditorProps.getValues())
  return (
    <React.Fragment>
      <IdealEditor {...idealEditorProps}/>
      <button onClick={getOnSubmit(jest.fn())}>
        Apply
      </button>
    </React.Fragment>
  )
}

function addGenerators(generators: string[]): void {
  const addGeneratorButton = screen.getByText("Add a generator")
  generators.forEach((generator, index) => {
    fireEvent.click(addGeneratorButton)
    const input = screen.getByTestId(`IdealEditorItem-input-${index}`)
    fireEvent.input(input, { target: { text: generator } })
  })
}

function apply(): void {
  const applyButton = screen.getByText("Apply")
  fireEvent.click(applyButton)
}

describe("IdealEditorTestContainer", () => {
  test("empty text as generator", async () => {
    const hookArgs: UseIdealEditorArgs = {
      idealJson: "[]",
      setIdealJson: jest.fn(),
      validateGenerator: jest.fn().mockResolvedValue(true),
      validateGeneratorArray: jest.fn().mockResolvedValue(true),
    }
    render(<IdealEditorTestContainer {...hookArgs}/>)

    addGenerators([""])
    apply()

    expect(await screen.findByRole("alert")).toContainHTML("Please enter the generator.")
  })

  test("parse error", async () => {
    const errorMessage = "This is the error message."
    const hookArgs: UseIdealEditorArgs = {
      idealJson: "[]",
      setIdealJson: jest.fn(),
      validateGenerator: jest.fn().mockResolvedValue(errorMessage),
      validateGeneratorArray: jest.fn().mockResolvedValue(true),
    }
    render(<IdealEditorTestContainer {...hookArgs}/>)

    addGenerators(["x"])
    apply()

    await waitFor(() => {
      expect(screen.getByRole("alert")).toContainHTML(errorMessage)
    })
    expect(await screen.findByRole("alert")).toContainHTML(errorMessage)
  })
})
