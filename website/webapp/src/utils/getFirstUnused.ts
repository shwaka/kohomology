export function getFirstUnused<T>({
  usedValues, candidates, fallback,
}: { usedValues: T[], candidates: T[], fallback: T }): T {
  for (const candidate of candidates) {
    if (!usedValues.includes(candidate)) {
      return candidate
    }
  }
  return fallback
}
