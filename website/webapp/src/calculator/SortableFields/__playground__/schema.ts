import { z } from "zod"

const personSchema = z.object({
  name: z.string().nonempty(),
  age: z.number().nonnegative(),
})

export type Person = z.infer<typeof personSchema>

const personArraySchema = z.array(personSchema)

export const peopleFormValueSchema = z.object({
  personArray: personArraySchema,
})

export type PeopleFormInput = z.infer<typeof peopleFormValueSchema>
