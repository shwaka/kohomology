// @ts-check

/* eslint-disable no-undef */ // 'require' is not defined
/* eslint-disable @typescript-eslint/no-var-requires */ // Require statement not part of import statement

const path = require("path")
const CopyPlugin = require("copy-webpack-plugin")

/**
   @param context {import('@docusaurus/types').LoadContext}
   @param options {import('@docusaurus/types').PluginOptions}
   @returns { import('@docusaurus/types').Plugin | Promise<import('@docusaurus/types').Plugin> }
 */
/* eslint-disable @typescript-eslint/explicit-function-return-type */
function myPlugin(context, options) {
  return {
    name: "my-plugin",
    configureWebpack(config, isServer, utils) {
      return {
        module: {
          rules: [
            {
              test: /\.kt$/i,
              use: "raw-loader",
            },
            {
              test: /\.worker\.js$/,
              use: {
                loader: "worker-loader",
                options: {
                  inline: "no-fallback"
                }
              }
            },
          ]
        },
        plugins: [
          new CopyPlugin({
            patterns: [
              { from: "../kohomology/build/dokka/html", to: "dokka" },
              { from: "../benchmark-data/core/dev/bench", to: "benchmark"}, // for dedicated page
            ]
          })
        ],
        resolve: {
          alias: {
            "@data": path.resolve(context.siteDir, "src/data"),
            "@components": path.resolve(context.siteDir, "src/components"),
            "@benchmark": path.resolve(context.siteDir, "../benchmark-data/core/dev/bench"),
            "@benchmark-website": path.resolve(context.siteDir, "../benchmark-data/website/dev/bench"),
          }
        }
      }
    }
  }
}

module.exports = myPlugin
