# CLAIM-RELEASE PROTOCOL PSEUDOCODE

```pseudocode
type PID = { ready : Boolean }

owner : atomic PID
Q : queue of PID

// Atomic GET operation
GET :: (atomic T) -> T
// Atomic SET operation
// Param 1 is the atomic object
// Param 2 is the new value to set
SET :: (atomic T, T) -> ()
// Atomic Compare-and-Swap (CAS) operation
// Param 1 is the atomic object
// Param 2 is the expected value
// Param 3 is the new value
// Returns success (was expected the same as the current value)
CAS :: (atomic T, T, T) -> Boolean

// Schedule operation
SCHEDULE(PID) -> ()
// YIELD operation
YIELD() -> ()

// Queue operations. T (type) must be nullable.
// Enqueue operation
ENQUEUE(queue of T, T) -> ()
// Peek operation
PEEK(queue of T) -> T
// Dequeue operation
DEQUEUE(queue of T) -> T

CLAIM(pid : PID) =
    pid.ready = false
    ENQUEUE(Q, pid)
    head = PEEK(Q)
    IF (head == pid) THEN
        IF (CAS(owner, NULL, pid)) THEN
            SCHEDULE(pid)
        ELSE
            local_own = GET(owner)
            IF (local_own == pid) THEN
                YIELD
            ELSE IF (local_own != NULL) THEN
                IF (CAS(owner, local_own, pid)) THEN
                    SCHEDULE(pid)
                ELSE
                    IF (CAS(owner, NULL, pid)) THEN
                        SCHEDULE(pid)
                    ELSE
                        YIELD
                    END IF
                END IF
            ELSE
                SET(owner, pid)
                SCHEDULE(pid)
            END IF
        END IF
    ELSE
        YIELD

RELEASE(pid : PID) =
    DEQUEUE(Q)
    head = PEEK(Q)
    IF (head == NULL) THEN
        CAS(owner, pid, NULL)
    ELSE
        IF (CAS(owner, pid, head)) THEN
            SCHEDULE(head)
        END IF
    END IF
```
