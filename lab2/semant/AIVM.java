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

    public Set<Configuration> step(Configuration inputConf)
    {
        final Configuration conf = inputConf.clone(); // make sure we never touch inputConf
        
        Set<Configuration> results = new HashSet<Configuration>();
        Inst inst = conf.code.peek();
        
        if (inst instanceof Push)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
            {
                Push push = (Push) inst;
                next.stack.push(new StackValue(ops.abs(Integer.parseInt(push.n))));
            }
            else
            {
                next.stack.push(new StackValue(SignExc.ERR_A));
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Add)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.add(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Sub)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.subtract(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Mult)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.multiply(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Div)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.divide(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Eq)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            
            next.stack.push(new StackValue(ops.eq(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Le)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.leq(v1.se, v2.se)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof And)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue v1 = next.stack.pop();
            StackValue v2 = next.stack.pop();
            next.stack.push(new StackValue(ops.and(v1.te, v2.te)));
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Branch)
        {
            Branch branch = (Branch) inst;
            StackValue v1 = conf.stack.get(0);
            
            Configuration then = conf.clone();
            then.code.pop();
            then.stack.pop();
            
            for (Inst c : branch.c1)
            {
                then.code.addLast(c);
            }
            
            Configuration els = conf.clone();
            els.code.pop();
            els.stack.pop();
            
            for (Inst c : branch.c2)
            {
                els.code.addLast(c);
            }
            
            Configuration err = conf.clone();
            err.code.pop();
            err.stack.pop();
            err.invalidstate = true;
            
            if (conf.invalidstate == false)
            {
                if (ops.possiblyTrue(v1.te))
                {
                    results.add(then);
                }
                
                if (ops.possiblyFalse(v1.te))
                {
                    results.add(els);
                }
                
                if (ops.possiblyBErr(v1.te))
                {
                    results.add(err);
                }
            }
            else
            {
                results.add(err);
            }
            
            return results;
        }
        else if (inst instanceof Loop)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
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
                
                prependCode(next.code, snippet);
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof AMTryCatch)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
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
                
                prependCode(next.code, snippet);
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Noop)
        {
            Configuration next = conf.clone();
            next.code.pop();
            results.add(next);
            return results;
        }
        else if (inst instanceof True)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
            {
                next.stack.push(new StackValue(ops.abs(true)));
            }
            else
            {
                next.stack.push(new StackValue(SignExc.ERR_A));
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof False)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
            {
                next.stack.push(new StackValue(ops.abs(false)));
            }
            else
            {
                next.stack.push(new StackValue(SignExc.ERR_A));
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Neg)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            StackValue val = next.stack.pop();
            
            next.stack.push(new StackValue(ops.neg(val.te)));
            results.add(next);
            return results;
        }
        else if (inst instanceof Check)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == true)
            {
                next.stack.push(new StackValue(ops.abs(true)));
            }
            else
            {
                next.stack.push(new StackValue(ops.abs(false)));
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Uncheck)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == true)
            {
                next.invalidstate = false;
            }
            
            results.add(next);
            return results;
        }
        else if (inst instanceof Store)
        {
            Store store = (Store) inst;
            StackValue val = conf.stack.get(0);
            
            Configuration store_val = conf.clone();
            store_val.code.pop();
            store_val.stack.pop();
            store_val.store.put(store.x, val.se);
            
            Configuration store_z = conf.clone();
            store_z.code.pop();
            store_z.stack.pop();
            store_z.store.put(store.x, SignExc.Z);
            
            Configuration err = conf.clone();
            err.code.pop();
            err.stack.pop();
            err.invalidstate = true;
            
            if (conf.invalidstate == false)
            {
                if (val.se == SignExc.ANY_A) // we dont want to store ANY because ERR <= ANY, so we store Z
                {
                    results.add(store_z);
                }
                else if (ops.possiblyInt(val.se)) // but we do want to store other non-error values
                {
                    results.add(store_val);
                }
                
                if (ops.possiblyAErr(val.se)) // ERR or ANY
                {
                    results.add(err);
                }
            }
            else
            {
                results.add(err);
            }
            
            return results;
        }
        else if (inst instanceof Fetch)
        {
            Configuration next = conf.clone();
            next.code.pop();
            
            if (conf.invalidstate == false)
            {
                Fetch fetch = (Fetch) inst;
                
                if (conf.store.containsKey(fetch.x))
                {
                    next.stack.push(new StackValue(conf.store.get(fetch.x)));
                }
                else
                {
                    next.stack.push(new StackValue(SignExc.Z));
                }
            }
            else
            {
                next.stack.push(new StackValue(SignExc.ERR_A));
            }
            
            results.add(next);
            return results;
        }
        else
        {
            System.out.println("Unknown instruction!!!!!!!!!! " + inst.getClass().getName());
            return null;
        }
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
