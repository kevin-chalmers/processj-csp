import java.util.Random;
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
        Random rand = new Random(System.currentTimeMillis());
        while(true)
        {
            try 
            {
                Thread.sleep(rand.nextLong(10, 500));
                if (!mut.claim(this))
                {
                    yielding();
                }
                System.out.println(ID);
                Thread.sleep(rand.nextLong(10, 500));
                mut.release(this);
            }
            catch (Exception e)
            {

            }
        }
    }
}
