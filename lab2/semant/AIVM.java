package semant;

import semant.amsyntax.*;
import semant.signexc.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;

class AIVM
{
    public static void main(String[] args)
    {
        new AIVM();
    }

    public Boolean invalidstate = false;

    public SignExcOps ops;

    public AIVM()
    {
        LinkedList<Inst> code = new LinkedList<Inst>();
        stack = new LinkedList<StackValue>();
        store = new HashMap<String, SignExc>();
        ops = new SignExcOps();

    }

    private void prependCode(LinkedList<Inst> target, Code snippet)
    {
        for (int i = snippet.size() - 1; i >= 0; --i)
        {
            target.addFirst(snippet.get(i));
        }
    }

    public Set<Configuration> step(Configuration conf)
    {
        Set<Configuration> result = new HashSet<Configuration>();
        Configuration res = conf.clone();

        Inst inst =  res.code.pop(); // get rid of leading instruction

        if (inst instanceof Push)
        {
            if (res.invalidstate == false)
            {
                Push push = (Push) inst;
                res.stack.push(new StackValue(ops.abs(Integer.parseInt(push.n))));
                // System.out.println("pushed " + push.n);
            }
        }
        else if (inst instanceof Add)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                res.stack.push(new StackValue(ops.add(z1.se,z2.se)));
            }

            // System.out.println("add");


        }
        else if (inst instanceof Sub)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                // System.out.println("sub");

                res.stack.push(new StackValue(ops.subtract(z1.se, z2.se)));
            }
        }
        else if (inst instanceof Mult)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                // System.out.println("mult");

                res.stack.push(new StackValue(ops.multiply(z1.se, z2.se)));
            }
        }
        else if (inst instanceof Div)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                //if (ops.possiblyInt(z2.se))
                //{
                //  invalidstate = true;
                //}
                //else
                //{
                res.stack.push(new StackValue(ops.divide(z1.se, z2.se)));
                //}
            }
        }
        else if (inst instanceof Eq)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                // System.out.println("eq");

                res.stack.push(new StackValue((ops.eq(z1.se, z2.se))));
            }
        }
        else if (inst instanceof Le)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                // System.out.println("le");
                res.stack.push(new StackValue(ops.leq(z1.se, z2.se)));
            }
        }
        else if (inst instanceof And)
        {
            if (res.invalidstate == false)
            {
                StackValue z1 = stack.pop();
                StackValue z2 = stack.pop();
                res.stack.push(new StackValue(ops.and(z1.te, z2.te)));
            }
        }
        else if (inst instanceof Branch)
        {
            Branch branch = (Branch) inst;

            StackValue z1 = stack.pop();
            if  (z1.boolval == true)
            {
                for (int i = branch.c1.size() -1; i >= 0; i--)
                {
                    code.addFirst(branch.c1.get(i));
                    // System.out.println("added " + branch.c1.get(i) + " to stack");
                }
            }
            else
            {
                for (int i = branch.c2.size() -1; i >= 0; i--)
                {
                    code.addFirst(branch.c2.get(i));
                    // System.out.println("added " + branch.c2.get(i) + " to stack");
                }
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

                prependCode(code, snippet);
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

                prependCode(code, snippet);
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
                // System.out.println("pushed tt");
            }
            else
            {
                res.stack.push(new StackValue(SignExc.ERR_A));
            }
        }
        else if (inst instanceof False)
        {
            if (res.invalidstate == false)
            {
                res.stack.push(new StackValue(ops.abs(false)));
                // System.out.println("pushed tt");
            }
            else
            {
                res.stack.push(new StackValue(SignExc.ERR_A));
            }
        }
        else if (inst instanceof Neg)
        {
            StackValue val = stack.pop();
            res.stack.push(new StackValue(ops.neg(val.te)));
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
                StackValue val = stack.pop();
                // System.out.println("stored " + val.intval);
                if (val.se == SignExc.ERR_A)
                {
                    res.invalidstate = true;
                }
                else if (val.se == SignExc.ANY_A)
                {
                    res.invalidstate = true;
                    result.add(res);
                    res.invalidstate = false;
                    res.store.put(storeinst.x, SignExc.Z);
                }
                else
                {
                    res.store.put(storeinst.x, val.se);
                }
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
                }
                else
                {
                    res.stack.push(new StackValue(res.store.get(fetch.x)));
                    // System.out.println("fetched " + fetch.x + " with value " + store.get(fetch.x));
                }
            }
        }
        else
        {
            System.out.println("Unknown instruction!!!!!!!!!! " + inst.getClass().getName());
        }
        return result;
    }

    public String Execute(LinkedList<Inst> code, Boolean step)
    {
        int lastpnt = 1;
        while (code.size() > 0)
        {
            // System.out.println("");
            // System.out.println("invalidstate is " + invalidstate);
            // System.out.println("code: ");
            // for (int i = 0; i < code.size(); i++)
            // {
            // System.out.print(code.get(i) + " ");
            // }
            // System.out.println("");
            // System.out.print("stack: ");
            for (int i = 0; i < stack.size(); i++)
            {
                System.out.print(stack.get(i) + " ");
            }
            System.out.println(store);
            // System.out.println("");



            Inst inst = code.pop();
            int ctrlpnt = inst.stmControlPoint;

            if (ctrlpnt == 0)
            {
                System.out.print("(" + lastpnt + ") ");
            }
            else
            {
                System.out.print("(" + inst.stmControlPoint + ") ");
                lastpnt = inst.stmControlPoint;
            }
            System.out.print(inst + " ");
            if (step == true)
            {
                try
                {
                    System.in.read();
                }
                catch(Exception e)
                {}
            }


        }
        System.out.println(store);
        return "Execution Done";
    }

    private LinkedList<StackValue> stack;
    private HashMap<String, SignExc> store;

    class Configuration implements Cloneable
    {
        public LinkedList<Inst> code;
        public LinkedList stack;
        public HashMap<String, SignExc> store;
        public Boolean invalidstate;

        public Configuration(LinkedList<Inst> code, LinkedList stack, HashMap store, Boolean invalidstate)
        {
            this.code = code;
            this.stack = stack;
            this.store = store;
            this.invalidstate = invalidstate;
        }

        public Configuration clone()
        {
            return new Configuration((LinkedList<Inst>) code.clone(), (LinkedList)stack.clone(), (HashMap)store.clone(), invalidstate);
        }
    }

    private class StackValue
    {
        public StackValue(SignExc sign)
        {
            this.se = sign;
            this.te = null;
        }

        public StackValue(TTExc ttex)
        {
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
}