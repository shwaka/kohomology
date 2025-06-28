// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

/* eslint-disable no-undef */ // 'require' is not defined

import rehypeKatex from "rehype-katex"
import remarkMath from "remark-math"

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: "Kohomology",
  tagline: "Cohomology calculator for Sullivan algebras",
  url: "https://shwaka.github.io",
  baseUrl: "/kohomology/", // ends with "/" (see https://docusaurus.io/docs/api/docusaurus-config#baseurl)
  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",
  favicon: "img/favicon.svg",
  organizationName: "shwaka", // Usually your GitHub org/user name.
  projectName: "kohomology", // Usually your repo name.

  presets: [
    [
      "@docusaurus/preset-classic",
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve("./sidebars.js"),
          // Please change this to your repo.
          editUrl: "https://github.com/shwaka/kohomology/edit/main/website/",
          remarkPlugins: [remarkMath],
          rehypePlugins: [rehypeKatex],
        },
        // blog: {
        //   showReadingTime: true,
        //   // Please change this to your repo.
        //   editUrl:
        //     'https://github.com/shwaka/kohomology/edit/main/website/blog/',
        // },
        theme: {
          customCss: require.resolve("./src/css/custom.scss"),
        },
      }),
    ],
  ],

  stylesheets: [
    {
      href: "https://cdn.jsdelivr.net/npm/katex@0.13.11/dist/katex.min.css",
      integrity: "sha384-Um5gpz1odJg5Z4HAmzPtgZKdTBHZdw8S29IecapCSB31ligYPhHQZMIlWLYQGVoc",
      crossorigin: "anonymous",
    },
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: "Kohomology",
        logo: {
          alt: "My Site Logo",
          src: "img/favicon.svg", // 上で baseUrl を設定するとうまくいかない？ local でのテスト時だけ？
        },
        items: [
          {
            type: "doc",
            docId: "intro",
            position: "left",
            label: "Docs",
          },
          // {to: '/blog', label: 'Blog', position: 'left'},
          { to: "calculator", label: "Calculator", position: "left" },
          {
            href: "/dokka/index.html",
            label: "API",
            position: "left",
            target: "_blank", // これがないと React router のせいでうまくいかないっぽい
          },
          { to: "others", label: "Others", position: "left" },
          {
            type: "custom-playground",
            position: "right",
          },
          {
            type: "custom-goToPublishedPage",
            position: "right",
          },
          {
            href: "https://github.com/shwaka/kohomology",
            label: "GitHub",
            position: "right",
          },
        ],
      },
      // footer: {
      //   style: "dark",
      //   links: [
      //     {
      //       title: "Docs",
      //       items: [
      //         {
      //           label: "Tutorial",
      //           to: "/docs/intro",
      //         },
      //       ],
      //     },
      //     // {
      //     //   title: 'Community',
      //     //   items: [
      //     //     {
      //     //       label: 'Stack Overflow',
      //     //       href: 'https://stackoverflow.com/questions/tagged/docusaurus',
      //     //     },
      //     //     {
      //     //       label: 'Discord',
      //     //       href: 'https://discordapp.com/invite/docusaurus',
      //     //     },
      //     //     {
      //     //       label: 'Twitter',
      //     //       href: 'https://twitter.com/docusaurus',
      //     //     },
      //     //   ],
      //     // },
      //     {
      //       title: "More",
      //       items: [
      //         // {
      //         //   label: 'Blog',
      //         //   to: '/blog',
      //         // },
      //         {
      //           label: "GitHub",
      //           href: "https://github.com/shwaka/kohomology",
      //         },
      //         {
      //           href: "/dokka/index.html",
      //           label: "dokka",
      //           target: "_blank", // これがないと React router のせいでうまくいかないっぽい
      //         },
      //         {
      //           href: "/benchmark/index.html",
      //           label: "benchmark",
      //           target: "_blank", // これがないと React router のせいでうまくいかないっぽい
      //         },
      //       ],
      //     },
      //   ],
      //   // copyright: `Copyright © ${new Date().getFullYear()} My Project, Inc. Built with Docusaurus.`,
      // },
      prism: {
        // themes are configured in the createConfig() function below
        additionalLanguages: ["kotlin", "groovy", "latex", "shell-session"],
      },
    }),

  plugins: [
    "./plugins/my-plugin.js",
    "docusaurus-plugin-sass",
  ],
}

/* eslint-disable @typescript-eslint/explicit-function-return-type */
async function createConfig() {
  const lightTheme = (await import("./src/prismThemes/prismLight.mjs")).default
  const darkTheme = (await import("./src/prismThemes/prismDark.mjs")).default
  // @ts-expect-error: we know it exists, right
  config.themeConfig.prism.theme = lightTheme
  // @ts-expect-error: we know it exists, right
  config.themeConfig.prism.darkTheme = darkTheme
  return config
}

module.exports = createConfig
