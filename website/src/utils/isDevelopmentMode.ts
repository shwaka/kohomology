export function isDevelopmentMode(): boolean {
  // https://docusaurus.io/docs/advanced/ssg#node-env
  return process.env.NODE_ENV === "development"
}
