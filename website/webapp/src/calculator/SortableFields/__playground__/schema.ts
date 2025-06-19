import { sumBy } from "remeda"
import { RefinementCtx, z } from "zod"

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
}).superRefine((val, ctx) => {
  addIssueForTotalAge(val.personArray, ctx)
})

export type PeopleFormInput = z.infer<typeof peopleFormValueSchema>

const minTotalAge = 300
function addIssueForTotalAge(val: Person[], ctx: RefinementCtx): void {
  const totalAge = sumBy(val, (person) => person.age)
  if (totalAge < minTotalAge) {
    ctx.addIssue({
      path: ["_global_errors", "totalAge"],
      code: z.ZodIssueCode.custom,
      message: `The sum of person.age must be at least {minTotalAge}, but was ${totalAge}`,
    })
  }
}
