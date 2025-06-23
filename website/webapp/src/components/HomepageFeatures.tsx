/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
import React, { ReactElement } from "react"

import Link from "@docusaurus/Link"
import clsx from "clsx"

import styles from "./HomepageFeatures.module.css"

type FeatureItem = {
  title: string
  description: ReactElement
}

const FeatureList: FeatureItem[] = [
  {
    title: "Compute cohomology efficiently",
    description: (
      <React.Fragment>
        Kohomology can compute the cohomology of a Sullivan algebra.
        Since it can be done
        with <Link href="https://en.wikipedia.org/wiki/Sparse_matrix">sparse matrices</Link>,
        we can compute complicated examples.
      </React.Fragment>
    )
  },
  {
    title: "DGA morphisms",
    description: (
      <React.Fragment>
        Kohomology can also compute DGA morphisms, especially from Sullivan algebras.
        Derivations on Sullivan algebras are also supported.
      </React.Fragment>
    )
  },
  {
    title: "Kotlin multiplatform library",
    description: (
      <React.Fragment>
        Kohomology is a <Link href="https://kotlinlang.org/">Kotlin</Link> <Link href="https://kotlinlang.org/docs/multiplatform.html">multiplatform</Link> library.
        You can use it in any Kotlin program.
        There is also an online <Link href="./calculator">Calculator</Link> powered
        by <Link href="https://kotlinlang.org/docs/js-overview.html">Kotlin/JS</Link>.
      </React.Fragment>
    )
  },
  // {
  //   title: "Output LaTeX code",
  //   description: (
  //     <>
  //       Kohomology can print LaTeX source code.
  //       It is useful to visualize comlicated computation result
  //       as highly readable equations typeset by LaTeX.
  //     </>
  //   )
  // },
]

function Feature({title, description}: FeatureItem): ReactElement {
  return (
    <div className={clsx("col col--4")}>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  )
}

export default function HomepageFeatures(): ReactElement {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  )
}
