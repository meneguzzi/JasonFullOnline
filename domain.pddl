(define (domain shopping-domain)
    (:requirements :strips)
    (:predicates
        (hasMoney)
        (hasPhone)
        (onPhone)
        (messageSent)
        (randomBelief1)
        (randomBelief2)
        (parentsHappy)
        (atGym)
        (motivated)
        (hungry)
        (happy)
        (inCar)
        (hasCar)
        (tired)
        (atWork)
        (bossHappy)
        (atHome)
    )

    (:action buyphone
        :precondition (hasMoney)
        :effect 
            (and 
                (not (hasMoney))
                (hasPhone)
            )
    )

    (:action dochores
        :effect 
            (hasMoney)
    )

    (:action earnsalary
        :effect 
            (hasMoney)
    )

    (:action textfriend
        :precondition 
                    (and 
                        (onPhone)
                        (hasPhone)
                    )
        :effect 
            (messageSent)
    )

    (:action usephone
        :precondition (hasPhone)
        :effect 
            (onPhone)
    )

    (:action goOffPhone
        :precondition 
            (and 
                (onPhone)
                (hasPhone)
            )
        :effect 
            (not (onPhone))
    )

    (:action goToGym
        :precondition 
            (and 
                (motivated)
                (inCar)
            )
        :effect 
            (and 
                (atGym)
                (hungry)
                (happy)
            )
    )
    (:action getInCar
        :precondition (hasCar)
        :effect 
            (and 
                (inCar)
                (not (atHome))
            )
    )
    (:action goToWork
        :precondition (inCar)
        :effect 
            (and 
                (atWork)
                (tired)
                (bossHappy)
            )
    )
)
