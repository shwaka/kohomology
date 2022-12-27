import { useTheme } from "@mui/material"

export function useMobileMediaQuery(): string {
  const theme = useTheme()
  return theme.breakpoints.down("sm")
}
