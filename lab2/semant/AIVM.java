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

    public void buildgraph(Configuration start, int endingControlPoint)
    {
        HashMap<Integer, GraphNode> graph = new HashMap<Integer, GraphNode>();
        Deque<Configuration> queue = new LinkedList<>();
        
        GraphNode startNode = new GraphNode();
        startNode.addConfig(start);
        
        graph.put(start.code.peek().stmControlPoint, startNode);
        
        System.out.println("Queing " + start.toString());
        queue.addLast(start);
        
        while (!queue.isEmpty())
        {
            Configuration v = queue.pollFirst();
            System.out.println("Exploring from " + v.toString());
            
            Set<Configuration> neighbors = step_to_next_controlpoint(v);
            
            for (Configuration u : neighbors)
            {
                int controlPoint;
                
                if (u.code.isEmpty())
                {
                    controlPoint = endingControlPoint;
                }
                else
                {
                    controlPoint = u.code.peek().stmControlPoint;
                }
                
                System.out.println(">>>> Neighbor (CP=" + controlPoint + "): " + u.toString());
                
                if (!graph.containsKey(controlPoint))
                {
                    graph.put(controlPoint, new GraphNode());
                }
                
                if (!graph.get(controlPoint).hasConfig(u))
                {
                    graph.get(controlPoint).addConfig(u);
                    
                    if (!u.code.isEmpty())
                    {
                        System.out.println(">>>> Queing " + u.toString());
                        queue.addLast(u);
                    }
                }
            }
        }
        
        System.out.println("queue is: " + queue);
        System.out.println("graph is: " + graph);
    }
    
    private class SCPJob
    {
        public SCPJob(Configuration config)
        {
            this.config = config;
            this.done = false;
        }
        
        public boolean equals(Object other)
        {
            if (other == this)
            {
                return true;
            }
            else if (!(other instanceof SCPJob))
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
            return this.config.hashCode();
        }
        
        public Configuration config;
        public boolean done;
    }

    public Set<Configuration> step_to_next_controlpoint(Configuration start)
    {
        Set<SCPJob> jobs = new HashSet<SCPJob>();
        Set<SCPJob> newJobs = new HashSet<SCPJob>();
        
        jobs.add(new SCPJob(start));
        
        while (true)
        {
            boolean workTodo = false;
            
            for (SCPJob job : jobs)
            {
                if (!job.done)
                {
                    workTodo = true;
                    break;
                }
            }
            
            if (!workTodo)
            {
                HashSet<Configuration> results = new HashSet<Configuration>();
                
                for (SCPJob job : jobs)
                {
                    results.add(job.config);
                }
                
                return results;
            }
            
            for (SCPJob job : jobs)
            {
                if (job.done)
                {
                    continue;
                }
                
                int ctrlpnt = job.config.code.peek().stmControlPoint;
                
                for (Configuration stepconf : step(job.config))
                {
                    SCPJob job2 = new SCPJob(stepconf);
                    
                    if (stepconf.code.isEmpty() || (stepconf.code.peek().stmControlPoint != ctrlpnt
                                                 && stepconf.code.peek().stmControlPoint > 0))
                    {
                        job2.done = true;
                    }
                    
                    newJobs.add(job2);
                }
            }
            
            jobs = newJobs;
            newJobs = new HashSet<SCPJob>();
        }
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
