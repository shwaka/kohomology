import path from "path"

import react from "@vitejs/plugin-react"
import { defineConfig } from "vitest/config"

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: "jsdom",
    exclude: ["node_modules"],
    setupFiles: ["./vitest-setup.ts"]
  },
  resolve: {
    alias: {
      "@docusaurus": path.resolve(__dirname, "__mocks__/@docusaurus"),
    }
  }
})
