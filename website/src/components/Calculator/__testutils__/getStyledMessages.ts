import { screen, within } from "@testing-library/react"

type ContainerType = "all" | "results" | "form"

function getContainer(containerType: ContainerType): HTMLElement {
  switch (containerType) {
    case "all":
      return document.body
    case "results":
      return screen.getByTestId("calculator-results")
    case "form":
      return screen.getByTestId("CalculatorForm-StackItem-DGA")
  }
}

export function getStyledMessages(containerType: ContainerType): string[] {
  const container = getContainer(containerType)
  const divArray: HTMLElement[] = within(container).getAllByTestId("show-styled-message")
  return divArray.map((div) => {
    const dataStyledMessage: string | null = div.getAttribute("data-styled-message")
    if (dataStyledMessage === null) {
      return "undefined"
    } else {
      return dataStyledMessage
    }
  })
}
