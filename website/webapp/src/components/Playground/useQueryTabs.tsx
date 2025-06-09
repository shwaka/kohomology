import React, { useCallback } from "react"

import { QueryTab } from "./QueryTab"
import { useQueryState } from "./useQueryState"

function isTabKey<K extends string>(tabKeys: K[], tabKey: string): tabKey is K {
  return (tabKeys as string[]).includes(tabKey)
}

function getTab<K extends string>(tabs: QueryTab<K>[], tabKey: K): QueryTab<K> {
  for (const tab of tabs) {
    if (tab.key === tabKey) {
      return tab
    }
  }
  throw new Error(`No tab found for key ${tabKey}`)
}

function getKeys<K extends string>(tabs: QueryTab<K>[]): K[] {
  return tabs.map((tab) => tab.key)
}

function useTabKey<K extends string>(tabKeys: K[]): [K, (newTabKey: string) => void] {
  const defaultTabKey: K = tabKeys[0]
  const [tabKeyString, setTabKeyString] = useQueryState("tab-key", defaultTabKey)
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

interface UseQueryTabsReturnValue {
  renderSelect: () => React.JSX.Element
  renderTabs: () => React.JSX.Element
}

export function useQueryTabs<K extends string>(tabs: QueryTab<K>[]): UseQueryTabsReturnValue {
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
