package semant;

import semant.amsyntax.*;
import semant.signexc.*;

import java.util.*;

class AIVM
{
    public static void main(String[] args)
    {
        new AIVM();
    }
    
    public SignExcOps ops;

    public AIVM()
    {
        ops = new SignExcOps();
    }

    private void prependCode(LinkedList<Inst> target, Code snippet)
    {
        for (int i = snippet.size() - 1; i >= 0; --i)
        {
            target.addFirst(snippet.get(i));
        }
    }

    public void buildgraph(Configuration start)
    {
        HashMap<Integer, GraphNode> graph = new HashMap<Integer, GraphNode>();

        Deque<Configuration> queue = new LinkedList<>();
        queue.addLast(start);

        while (!queue.isEmpty())
        {
            Configuration v = queue.pollFirst();
            GraphNode x = new GraphNode();
            x.addConfig(v);
            System.out.println(">>>> Queueing Configuration(" + x.configs + ")");
            System.out.println(">>>> Exploring from Configuration(" + x.configs+ ")");
            graph.put(v.code.peek().stmControlPoint, x);
            Set<Configuration>neighbors = step_to_next_controlpoint(v);

            for (Configuration u : neighbors)
            {
                int controlpoint = u.code.peek().stmControlPoint;
                System.out.println(">>>> Neighbour (CP=" + controlpoint + ") : Configuration(" + u + ")");
                //System.out.println("not graphcontainsvalue u.stmcontrol? " + !graph.containsKey(u.code.peek().stmControlPoint));
                //System.out.println("not graph.u.stmcontrol = u? " + !graph.get(u.code.peek().stmControlPoint).equals(u));
                //System.out.println("graph is " + graph.toString());
                //System.out.println(u.code.peek().stmControlPoint + " is not in graph");
                if (!graph.containsKey(controlpoint))
                {
                    graph.put(controlpoint, new GraphNode());
                    System.out.println(">>>> Added (CP=" + controlpoint + ") : Configuration" + u + " to the graph");
                }
                if (!graph.get(controlpoint).hasConfig(u))
                {
                    graph.get(controlpoint).addConfig(u);
                    queue.addLast(u);
                }
            }
        }
        System.out.println("queue is: " + queue);
        System.out.println("graph is: " + graph);
    }

    public Set<Configuration> step_to_next_controlpoint(Configuration start)
    {
        Set<Configuration> jobs = new HashSet<Configuration>();
        Set<Configuration> next = new HashSet<Configuration>();

        //System.out.println("jobs before adding the start config: " + jobs);
        jobs.add(start);
        //System.out.println("jobs after adding the start config: " + jobs);
        boolean done = false;
        int count = 0;
        while (!done)
        {
            int donecount = 0;
            for(Configuration things : jobs) {
                if (things.done) {
                    donecount++;
                }
            }
            if (donecount == jobs.size())
            {
                done = true;
                return jobs;
            }
            else {
                //System.out.println("jobs is: " + jobs);
                int ctrlpnt = 0;
                //System.out.println("length of jobs is: " + jobs.size());
                for (Configuration job : jobs) {
                    ctrlpnt = job.code.peek().stmControlPoint;
                    next = step(job);
                    //System.out.println("next after next = step(job): " + next);

                    //System.out.println(jobs);
                }

                for (Configuration job2 : next) {
                    //System.out.println("inside the for loop looking at job2: " + job2);
                    if (job2.code.isEmpty() || (job2.code.peek().stmControlPoint != ctrlpnt && job2.code.peek().stmControlPoint > 0)) {
                        //System.out.println("startcontrolpoint = " + ctrlpnt + " and this controlpoins = " + job2.code.peek().stmControlPoint);
                        job2.done = true;
                        //System.out.println("this job is done");
                    }
                    //System.out.println("added " + job2.toString() + " to jobs");
                    jobs.clear();
                    jobs.add(job2);
                    //System.out.println("this is jobs after added to jobs: " + jobs);
                }
            }

            count++;
            //System.out.println("iteration " + count);
        }
        return jobs;
    }

    public Set<Configuration> step(Configuration conf)
    {
        Set<Configuration> result = new HashSet<Configuration>();
        Configuration res = conf.clone();

        int lastpnt = 1;

        Inst inst =  res.code.pop(); // get rid of leading instruction

        int ctrlpnt = inst.stmControlPoint;

        if (ctrlpnt == 0)
        {
            //System.out.print("(" + lastpnt + ") ");
        }
        else
        {
            //System.out.print("(" + inst.stmControlPoint + ") ");
            //lastpnt = inst.stmControlPoint;
        }

        if (inst instanceof Push)
        {
            if (res.invalidstate == false)
            {
                Push push = (Push) inst;
                res.stack.push(new StackValue(ops.abs(Integer.parseInt(push.n))));
                result.add(res);
            }
        }
        else if (inst instanceof Add)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                res.stack.push(new StackValue(ops.add(z1.se,z2.se)));
                result.add(res);
            }

            System.out.println("add");


        }
        else if (inst instanceof Sub)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                System.out.println("sub");

                res.stack.push(new StackValue(ops.subtract(z1.se, z2.se)));
                result.add(res);
            }
        }
        else if (inst instanceof Mult)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                // System.out.println("mult");

                res.stack.push(new StackValue(ops.multiply(z1.se, z2.se)));
                result.add(res);
            }
        }
        else if (inst instanceof Div)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                //if (ops.possiblyInt(z2.se))
                //{
                //  invalidstate = true;
                //}
                //else
                //{
                res.stack.push(new StackValue(ops.divide(z1.se, z2.se)));
                result.add(res);
                //}
            }
        }
        else if (inst instanceof Eq)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                //System.out.println("eq of " + z1.se + " and " +  z2.se + " is: " + ops.eq(z1.se, z2.se));

                res.stack.push(new StackValue((ops.eq(z1.se, z2.se))));
                result.add(res);
            }
        }
        else if (inst instanceof Le)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                // System.out.println("le");
                res.stack.push(new StackValue(ops.leq(z1.se, z2.se)));
                result.add(res);
            }
        }
        else if (inst instanceof And)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = res.stack.pop();
                StackValue z2 = res.stack.pop();
                res.stack.push(new StackValue(ops.and(z1.te, z2.te)));
                result.add(res);
            }
        }
        else if (inst instanceof Branch)
        {
            Branch branch = (Branch) inst;

            StackValue z1 = res.stack.pop();
            if (z1.te == TTExc.TT)
            {
                for (int i = branch.c1.size() -1; i >= 0; i--) {
                    res.code.addFirst(branch.c1.get(i));
                }
                result.add(res);
            }
            if (z1.te == TTExc.FF)
            {
                for (int i = branch.c2.size() -1; i >= 0; i--) {
                    res.code.addFirst(branch.c2.get(i));
                }
                result.add(res);
            }

            if  (ops.possiblyTrue(z1.te))
            {
                Configuration newres = res.clone();

                for (int i = branch.c1.size() -1; i >= 0; i--)
                {
                    newres.code.addFirst(branch.c1.get(i));
                }
                result.add(newres);
            }
            if (ops.possiblyFalse(z1.te))
            {
                for (int i = branch.c2.size() -1; i >= 0; i--)
                {
                    res.code.addFirst(branch.c2.get(i));
                }
                result.add(res);
            }
            if (ops.possiblyBErr(z1.te))
            {
                res.invalidstate = true;
                result.add(res);

            }
        }

        else if (inst instanceof Loop)
        {
            if (res.invalidstate == false)
            {
                Loop loop = (Loop) inst;
                Code snippet = new Code();
                snippet.addAll(loop.c1);

                Code brtrue = new Code();
                brtrue.addAll(loop.c2);
                brtrue.add(inst);

                Code brfalse = new Code();
                brfalse.add(new Noop());

                snippet.add(new Branch(brtrue, brfalse));

                prependCode(res.code, snippet);
            }
        }
        else if (inst instanceof AMTryCatch)
        {
            if (res.invalidstate == false)
            {
                AMTryCatch trycatch = (AMTryCatch) inst;
                Code snippet = new Code();
                snippet.addAll(trycatch.c1);
                snippet.add(new Check());
                Code brtrue = new Code();
                brtrue.add(new Uncheck());
                brtrue.addAll(trycatch.c2);

                Code brfalse = new Code();
                brfalse.add(new Noop());

                snippet.add(new Branch(brtrue, brfalse));

                prependCode(res.code, snippet);
            }
        }

        else if (inst instanceof Noop)
        {
            // System.out.println("noop");
        }
        else if (inst instanceof True)
        {
            if (res.invalidstate == false)
            {
                res.stack.push(new StackValue(ops.abs(true)));
                result.add(res);
                // System.out.println("pushed tt");
            }
            else
            {
                res.stack.push(new StackValue(SignExc.ERR_A));
                result.add(res);
            }
        }
        else if (inst instanceof False)
        {
            if (res.invalidstate == false)
            {
                res.stack.push(new StackValue(ops.abs(false)));
                result.add(res);
                // System.out.println("pushed tt");
            }
            else
            {
                res.stack.push(new StackValue(SignExc.ERR_A));
                result.add(res);
            }
        }
        else if (inst instanceof Neg)
        {
            StackValue val = res.stack.pop();
            res.stack.push(new StackValue(ops.neg(val.te)));
            result.add(res);
        }
        /*
        else if (inst instanceof Check)
        {
            // System.out.println("invalidstate is " + invalidstate);
            if (invalidstate == true)
            {
                stack.push(new StackValue(true));
            }
            else
            {
                stack.push(new StackValue(false));
            }

        }

        else if (inst instanceof Uncheck)
        {
            // System.out.println("invalidstate is " + invalidstate);
            if (invalidstate == true)
            {
                invalidstate = false;
            }
        }
        */
        else if (inst instanceof Store)
        {
            if (res.invalidstate == false)
            {
                Store storeinst = (Store) inst;
                StackValue val = res.stack.pop();
                // System.out.println("stored " + val.intval);
                if (val.se == SignExc.ERR_A)
                {
                    res.invalidstate = true;
                    result.add(res);
                }
                else if (val.se == SignExc.ANY_A)
                {
                    Configuration newres = res.clone();
                    res.invalidstate = true;
                    result.add(res);
                    newres.invalidstate = false;
                    newres.store.put(storeinst.x, SignExc.Z);
                    result.add(newres);
                }
                else
                {
                    res.store.put(storeinst.x, val.se);
                    result.add(res);
                    System.out.println("stored: " + val.se);
                }
            }
            else
            {
                res.invalidstate = true;
                result.add(res);
            }
        }
        else if (inst instanceof Fetch)
        {
            if (res.invalidstate == false)
            {
                Fetch fetch = (Fetch) inst;
                if (res.store.get(fetch.x) == SignExc.ERR_A)
                {
                    res.invalidstate = true;
                    result.add(res);
                }
                else {
                    if (res.store.get(fetch.x) == null) {
                        res.stack.push(new StackValue(SignExc.Z));
                        result.add(res);
                    } else {
                        res.stack.push(new StackValue(res.store.get(fetch.x)));
                        result.add(res);
                        System.out.println("fetched " + fetch.x + " with value " + res.store.get(fetch.x));
                    }
                }
            }
        }
        else
        {
            System.out.println("Unknown instruction!!!!!!!!!! " + inst.getClass().getName());
        }
        //System.out.println("result is: " + result);
        return result;
    }

    public int getNumControlPoints(Collection<Inst> code)
    {
        return getNumControlPoints(code, 0);
    }
    
    public int getNumControlPoints(Collection<Inst> code, int oldcp)
    {
        int count = 0;

        for (Inst in : code)
        {
            if (in instanceof Branch)
            {
                count += getNumControlPoints(((Branch)in).c1, oldcp);
                count += getNumControlPoints(((Branch)in).c2, oldcp);
            }
            else if (in instanceof Loop)
            {
                count += getNumControlPoints(((Loop)in).c1, oldcp);
                count += getNumControlPoints(((Loop)in).c2, oldcp);
            }
            else if (in instanceof AMTryCatch)
            {
                count += getNumControlPoints(((AMTryCatch)in).c1, oldcp);
                count += getNumControlPoints(((AMTryCatch)in).c2, oldcp);
            }

            if (in.stmControlPoint > oldcp)
            {
                // System.out.println("Found CP " + in.stmControlPoint + " at " + in);
                ++count;
                oldcp = in.stmControlPoint;
            }
        }

        return count;
    }

    public void Execute(LinkedList<Inst> code)
    {
        Configuration start = new Configuration(code, new LinkedList<StackValue>(),
                                                new HashMap<String, SignExc>(), false);
        buildgraph(start, getNumControlPoints(code) + 1);
    }

    class Configuration implements Cloneable
    {
        public LinkedList<Inst> code;
        public LinkedList<StackValue> stack;
        public HashMap<String, SignExc> store;
        public Boolean invalidstate;

        public Configuration(LinkedList<Inst> code, LinkedList<StackValue> stack,
                              HashMap<String, SignExc> store, Boolean invalidstate)
        {
            this.code = code;
            this.stack = stack;
            this.store = store;
            this.invalidstate = invalidstate;
        }

        public Configuration clone()
        {
            return new Configuration((LinkedList<Inst>)code.clone(),
                                     (LinkedList<StackValue>)stack.clone(),
                                     (HashMap<String, SignExc>)store.clone(),
                                     invalidstate);
        }

        public String toString()
        {
            return "Configuration(code: " + code + " stack: " + stack + " store: " + store +
                   " state: " + invalidstate + ")";
        }
        
        public boolean equals(Object other)
        {
            if (other == this)
            {
                return true;
            }
            else if (!(other instanceof Configuration))
            {
                return false;
            }
            else
            {
                return this.hashCode() == other.hashCode();
            }
        }
        
        public int hashCode()
        {
            return this.toString().hashCode();
        }
    }

    private class StackValue
    {
        public StackValue(SignExc sign)
        {
            if (sign == null)
            {
                System.err.println("NULL SIGN IS NOT ACCEPTABLE");
                new Exception().printStackTrace();
                System.exit(1);
            }
            
            this.se = sign;
            this.te = null;
        }

        public StackValue(TTExc ttex)
        {
            if (ttex == null)
            {
                System.err.println("NULL TTEX IS NOT ACCEPTABLE");
                new Exception().printStackTrace();
                System.exit(1);
            }
            
            this.se = null;
            this.te = ttex;
        }

        public String toString()
        {
            return (te == null) ? se.toString() : te.toString();
        }

        public final SignExc se;
        public final TTExc te;
    }

    class GraphNode
    {
        public GraphNode()
        {
            this.configs = new HashSet<Configuration>();
            this.storeLUB = new HashMap<String, SignExc>();
        }

        public void addConfig(Configuration config)
        {
            this.configs.add(config);
        }

        public boolean hasConfig(Configuration config)
        {
            return this.configs.contains(config);
        }

        public void computeLUBs()
        {
            // TODO ..
        }
        
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            
            sb.append("GraphNode(");
            
            sb.append("configs(");
            sb.append(this.configs.toString());
            sb.append(")");
            
            sb.append(",");
            
            sb.append("storeLUB(");
            sb.append(this.storeLUB.toString());
            sb.append(")");
            
            sb.append(")");
            
            return sb.toString();
        }

        private Set<Configuration> configs;
        private HashMap<String, SignExc> storeLUB;
    }
}
