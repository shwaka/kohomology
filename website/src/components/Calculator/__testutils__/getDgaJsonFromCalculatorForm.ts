import { screen } from "@testing-library/react"

export function getDgaJsonFromCalculatorForm(): string | null {
  const div: HTMLElement | null = screen.queryByTestId("CalculatorForm-dga-info")
  if (div === null) {
    return null
  }
  const json: string | null = div.getAttribute("data-dga-json")
  return json
}
