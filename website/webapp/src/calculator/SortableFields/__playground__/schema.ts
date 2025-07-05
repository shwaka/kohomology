import { sumBy } from "remeda"
import { z } from "zod/v4"
import { $ZodRawIssue } from "zod/v4/core"

const personSchema = z.object({
  name: z.string().nonempty(),
  age: z.number().nonnegative(),
})

export type Person = z.infer<typeof personSchema>

const personArraySchema = z.array(personSchema)

const globalErrorsSchema = z.object({
  totalAge: z.null(),
})

export const peopleFormValueSchema = z.object({
  personArray: personArraySchema,
  _global_errors: globalErrorsSchema.optional(),
}).check((ctx) => {
  // Need to call trigger() manually to run global validation
  addIssueForTotalAge(ctx.value.personArray, ctx.issues)
})

export type PeopleFormInput = z.infer<typeof peopleFormValueSchema>

const minTotalAge = 300
function addIssueForTotalAge(val: Person[], issues: $ZodRawIssue[]): void {
  const totalAge = sumBy(val, (person) => person.age)
  if (totalAge < minTotalAge) {
    issues.push({
      input: val,
      path: ["_global_errors", "totalAge"],
      code: "custom",
      message: `The sum of person.age must be at least {minTotalAge}, but was ${totalAge}`,
      continue: true,
    })
  }
}
