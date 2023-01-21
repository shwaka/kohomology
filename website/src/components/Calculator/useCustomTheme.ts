import { useColorMode } from "@docusaurus/theme-common"
import { createTheme, Theme } from "@mui/material"
import { useMemo } from "react"

export function useCustomTheme(): Theme {
  const { colorMode } = useColorMode()

  const theme = useMemo(
    () => createTheme({
      palette: {
        mode: colorMode,
        primary: {
          main: "#7e6ca8", // --ifm-color-primary in src/css/custom.css
        }
      }
    }),
    [colorMode]
  )

  return theme
}
