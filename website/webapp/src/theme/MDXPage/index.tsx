import type { ReactNode } from "react"
import { Fragment } from "react"

import type { WrapperProps } from "@docusaurus/types"
import { getTitleWithDev } from "@theme/getTitleWithDev"
import type MDXPageType from "@theme/MDXPage"
import MDXPage from "@theme-original/MDXPage"

type Props = WrapperProps<typeof MDXPageType>
type Content = Props["content"] // LoadedMDXContent<...>

function getContentWithUpdatedTitle(content: Content): Content {
  // export type LoadedMDXContent<FrontMatter, Metadata, Assets = undefined> = {
  //   ...
  //   readonly metadata: Metadata;
  //   ...
  //   (): ReactNode; // <- content is callable!
  // };

  // Since content is callable (see the above def of LoadedMDXContent),
  // we use Object.assign (together with the spread operator).
  return Object.assign(
    () => content(),
    {
      ...content,
      metadata: {
        ...content.metadata,
        title: getTitleWithDev(content.metadata.title),
      }
    }
  )
}

function getPropsWithUpdatedTitle(props: Props): Props {
  return {
    ...props,
    content: getContentWithUpdatedTitle(props.content),
  }
}

export default function MDXPageWrapper(props: Props): ReactNode {
  return (
    <Fragment>
      <MDXPage {...getPropsWithUpdatedTitle(props)} />
    </Fragment>
  )
}
