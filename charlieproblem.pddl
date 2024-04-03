(define (problem texting-problem)
  (:domain shopping-domain)
  (:objects
    agent - person
  )
  (:init
    (hasMoney)
    (hasPhone)
    (parentsHappy)
    (onPhone)
  )
  (:goal
    (and
      (messageSent)
    )
  )
)
