import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Mutex 
{
    private ConcurrentLinkedQueue<Process> waitQueue = new ConcurrentLinkedQueue<Process>();

    private AtomicReference<Process> owner = new AtomicReference<Process>(null);

    public boolean claim(Process p)
    {
        p.currentState.set(Process.State.ENGAGING);
        waitQueue.add(p);
        Process head = waitQueue.peek();
        if (head == p)
        {
            if (owner.compareAndSet(null, p))
            {
                p.currentState.set(Process.State.ACTIVE);
                return true;
            }
            else
            {
                Process local = owner.get();
                if (local == null)
                {
                    owner.set(p);
                    p.currentState.set(Process.State.ACTIVE);
                    return true;
                }
                else if (local == p && p.currentState.compareAndSet(Process.State.ENGAGING, Process.State.WAITING))
                {
                    return false;
                }
                else if (local == p)
                {
                    p.currentState.set(Process.State.ACTIVE);
                    return true;
                }
                else if (owner.compareAndSet(local, p))
                {
                    p.currentState.set(Process.State.ACTIVE);
                    return true;
                }
                else if (owner.compareAndSet(null, p))
                {
                    p.currentState.set(Process.State.ACTIVE);
                    return true;
                }
                else if (p.currentState.compareAndSet(Process.State.ENGAGING, Process.State.WAITING))
                {
                    return false;
                }
                else
                {
                    p.currentState.set(Process.State.ACTIVE);
                    return true;
                }
            }
        }
        else if (p.currentState.compareAndSet(Process.State.ENGAGING, Process.State.WAITING))
        {
            return false;
        }
        else
        {
            p.currentState.set(Process.State.ACTIVE);
            return true;
        }
    }

    public void release(Process p)
    {
        waitQueue.poll();
        Process head = waitQueue.peek();
        if (head == null)
        {
            owner.compareAndSet(p, null);
        }
        else if (owner.compareAndSet(p, head))
        {
            head.schedule();
        }
    }
}
