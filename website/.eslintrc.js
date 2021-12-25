module.exports = {
  "env": {
    "browser": true,
    "es2021": true
  },
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:import/errors"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaFeatures": {
      "jsx": true
    },
    "ecmaVersion": 13,
    "sourceType": "module"
  },
  "plugins": [
    "react",
    "react-hooks",
    "@typescript-eslint",
    "unused-imports"
  ],
  "settings": {
    "react": {
      "version": "detect" // for eslint-plugin-react
    }
  },
  "rules": {
    "indent": [
      "error",
      2,
      {"SwitchCase": 1},
    ],
    "linebreak-style": [
      "error",
      "unix"
    ],
    "quotes": [
      "error",
      "double"
    ],
    "semi": [
      "error",
      "never"
    ],
    "@typescript-eslint/explicit-function-return-type": [
      2,
      { "allowExpressions": true }
    ],
    "@typescript-eslint/no-inferrable-types": "off",
    "@typescript-eslint/no-unused-vars": "off", // provided by "unused-imports"
    "unused-imports/no-unused-imports": "error",
    "unused-imports/no-unused-vars": [
      "warn",
      { "vars": "all", "varsIgnorePattern": "^_", "args": "after-used", "argsIgnorePattern": "^_" }
    ],
    "sort-imports": 0,
    "import/order": [2, { "alphabetize": { "order": "asc" } }],
    "import/named": 0, // Language not found in 'prism-react-renderer'
    "import/no-unresolved": 0, // ちゃんと設定できてないせいか大量に出てきてしまう
  }
}
