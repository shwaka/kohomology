// DSV = Dot-Separated Values (immitating CSV = Comma-Separated Values)

// Similar API to JSON (JSON.parse and JSON.stringify)
export const DSV = {
  parse: (dsv: string): string[] => dsv.split("."),
  stringify: (value: string[]): string => value.join(".")
}
