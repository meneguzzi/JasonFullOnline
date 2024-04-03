(define (problem texting-problem)
  (:domain shopping-domain)
  (:objects
    agent - person
  )
  (:init
    (hasPhone)
    (parentsHappy)
  )
  (:goal
    (and
      (onPhone)
    )
  )
)
