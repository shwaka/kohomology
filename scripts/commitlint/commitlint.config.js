// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

/** @type {import('@commitlint/types').UserConfig} */
const config = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "scope-empty": [2, "never"], // scope is required
    "scope-enum": [2, "always", ["core", "website", "profile", "misc"]],
    "header-max-length": [0],
  },
}

module.exports = config
