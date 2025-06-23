import React, { useCallback, ReactElement } from "react"

import { useLocalStorage } from "@site/src/utils/useLocalStorage"

import { SimpleTab } from "./SimpleTab"

function isTabKey<K extends string>(tabKeys: K[], tabKey: string): tabKey is K {
  return (tabKeys as string[]).includes(tabKey)
}

function getTab<K extends string>(tabs: SimpleTab<K>[], tabKey: K): SimpleTab<K> {
  for (const tab of tabs) {
    if (tab.key === tabKey) {
      return tab
    }
  }
  throw new Error(`No tab found for key ${tabKey}`)
}

function getKeys<K extends string>(tabs: SimpleTab<K>[]): K[] {
  return tabs.map((tab) => tab.key)
}

function useTabKey<K extends string>(tabKeys: K[]): [K, (newTabKey: string) => void] {
  const defaultTabKey: K = tabKeys[0]
  // const [tabKeyString, setTabKeyString] = useQueryState("tab-key", defaultTabKey)
  const [tabKeyString, setTabKeyString] = useLocalStorage("tab-key", defaultTabKey)
  const tabKey: K = isTabKey(tabKeys, tabKeyString) ? tabKeyString : defaultTabKey
  const setTabKey = useCallback((newTabKey: string) => {
    if (isTabKey(tabKeys, newTabKey)) {
      setTabKeyString(newTabKey)
    } else {
      throw new Error(`${newTabKey} is not a valid key for PlaygroundTab`)
    }
  }, [setTabKeyString, tabKeys])
  return [tabKey, setTabKey]
}

interface UseSimpleTabsReturnValue {
  renderSelect: () => ReactElement
  renderTabs: () => ReactElement
}

export function useSimpleTabs<K extends string>(tabs: SimpleTab<K>[]): UseSimpleTabsReturnValue {
  const tabKeys = getKeys(tabs)
  const [tabKey, setTabKey] = useTabKey(tabKeys)
  const tab = getTab(tabs, tabKey)

  return {
    renderSelect: () => (
      <select
        value={tabKey}
        onChange={(e) => setTabKey(e.target.value)}
      >
        {tabs.map((tab) => (
          <option value={tab.key} key={tab.key}>
            {tab.name}
          </option>
        ))}
      </select>
    ),
    renderTabs: () => tab.render(),
  }
}
