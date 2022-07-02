/** @type {import("ts-jest/dist/types").InitialOptionsTsJest} */
module.exports = {
  preset: "ts-jest",
  testEnvironment: "jsdom",
  transform: {
    "^.+\\.(ts|tsx)$": "ts-jest",
    "^.+\\.(js|jsx)$": "babel-jest",
  },
  setupFilesAfterEnv: ["<rootDir>/jest-setup.ts"],
};
