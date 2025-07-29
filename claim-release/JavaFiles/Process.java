import java.util.concurrent.atomic.AtomicReference;

public class Process extends Thread
{
    public enum State
    {
        WAITING,
        ENGAGING,
        SCHEDULED,
        ACTIVE
    }

    public int ID;

    public AtomicReference<State> currentState = new AtomicReference<State>(State.ACTIVE);

    private Mutex mut;

    public Process(int ID, Mutex mut)
    {
        this.ID = ID;
        this.mut = mut;
    }

    public void yielding()
    {
        while (!(currentState.get() == State.SCHEDULED));
        currentState.set(State.ACTIVE);
    }

    public void schedule()
    {
        if (!currentState.compareAndSet(State.ENGAGING, State.SCHEDULED))
        {
            currentState.compareAndSet(State.WAITING, State.SCHEDULED);
        }
    }

    public void run()
    {
        while(true)
        {
            if (!mut.claim(this))
            {
                yielding();
            }
            System.out.println(ID);
            mut.release(this);
        }
    }
}
