public class Main 
{
    public static void main(String[] args)
    {
        Mutex mut = new Mutex();
        Process[] procs = new Process[4];
        for (int i = 1; i <= 4; ++i)
        {
            procs[i - 1] = new Process(i, mut);
            procs[i - 1].start();
        }
    }
}
