import React, { useCallback, useState } from "react"

interface PlaygroundTab {
  key: string
  name: string
  render: () => React.JSX.Element
}

const tabs = [
  {
    key: "default",
    name: "Default",
    render: () => (<div>This is the default tab.</div>)
  },
  {
    key: "another",
    name: "Another",
    render: () => (<div>This is another tab.</div>)
  },
] as const satisfies PlaygroundTab[]

type TabKey = (typeof tabs)[number]["key"]

const tabKeys: TabKey[] = tabs.map((tab) => tab.key)
function isTabKey(tabKey: string): tabKey is TabKey {
  return (tabKeys as string[]).includes(tabKey)
}

function getTab(tabKey: TabKey): PlaygroundTab {
  for (const tab of tabs) {
    if (tab.key === tabKey) {
      return tab
    }
  }
  throw new Error(`No tab found for key ${tabKey}`)
}

export function Playground(): React.JSX.Element {
  const [tabKey, setTabKey] = useState<TabKey>(tabs[0].key)
  const tab = getTab(tabKey)
  const updateTabKey = useCallback((tabKey: string) => {
    if (isTabKey(tabKey)) {
      setTabKey(tabKey)
    } else {
      throw new Error(`${tabKey} is not a valid key for PlaygroundTab`)
    }
  }, [setTabKey])
  return (
    <div>
      This is playground.
      <select
        value={tabKey}
        onChange={(e) => updateTabKey(e.target.value)}
      >
        {tabs.map((tab) => (
          <option value={tab.key}>
            {tab.name}
          </option>
        ))}
      </select>
      {tab.render()}
    </div>
  )
}
