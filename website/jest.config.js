/** @type {import("ts-jest/dist/types").InitialOptionsTsJest} */
module.exports = {
  preset: "ts-jest",
  testEnvironment: "jsdom",
  transform: {
    "^.+\\.(ts|tsx)$": "ts-jest",
    "^.+\\.(js|jsx)$": "babel-jest",
  },
  setupFilesAfterEnv: ["<rootDir>/jest-setup.ts"],
  globals: {
    "ts-jest": {
      // "isolatedModules: true" disables type checking on "npm test".
      // This is necessary to use arrays in tests. (Why?)
      isolatedModules: true,
    }
  },
  modulePathIgnorePatterns: [
    "<rootDir>/kohomology-js/build/js/packages/kohomology-js",
    "<rootDir>/kohomology-js/build/tmp",
  ],
};
