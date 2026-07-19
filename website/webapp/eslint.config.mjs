import { eslintConfigShwakaReact } from "@shwaka/eslint-config-shwaka"
import testingLibrary from "eslint-plugin-testing-library"
import jestDom from "eslint-plugin-jest-dom"
import tseslint from 'typescript-eslint'
import { fixupConfigRules } from "@eslint/compat"
import { FlatCompat } from "@eslint/eslintrc"

const flatCompat = new FlatCompat()

export default tseslint.config(
  ...eslintConfigShwakaReact,
  {
    // "ignores" must be separated from "files"
    ignores: ["**/prismThemes/*.mjs"],
  },
  {
    files: [
      "**/*.js", "**/*.jsx", "**/*.mjs", "**/*.cjs",
      "**/*.ts", "**/*.tsx", "**/*.mts", "**/*.cts",
    ],
  },
  ...fixupConfigRules(
    flatCompat.extends(
      "plugin:testing-library/react",
      "plugin:jest-dom/recommended",
    )
  ),
  {
    rules: {
      "@typescript-eslint/no-deprecated": "warn",
    }
  },
  {
    // Enable type-aware linting for this standalone Docusaurus configuration file.
    // Since it is not included in a tsconfig.json, allowDefaultProject lets the
    // TypeScript project service provide type information for typed ESLint rules.
    files: ["docusaurus.config.mjs"],
    languageOptions: {
      parserOptions: {
        projectService: {
          allowDefaultProject: ["docusaurus.config.mjs"],
        },
        tsconfigRootDir: import.meta.dirname,
      },
    },
  },
)
