package semant;

import semant.amsyntax.*;
import semant.signexc.*;
import java.util.LinkedList;
import java.util.HashMap;

class VM
{
    public static void main(String[] args)
    {
        new VM();
    }
    
    public Boolean invalidstate = false;
    
    public VM()
    {
        LinkedList<Inst> code = new LinkedList<Inst>();
        stack = new LinkedList<StackValue>();
        store = new HashMap<String, Integer>();

    }
    
    private void prependCode(LinkedList<Inst> target, Code snippet)
    {
          for (int i = snippet.size() - 1; i >= 0; --i)
          {
                  target.addFirst(snippet.get(i));
          }
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
                    //System.out.print("(" + lastpnt + ") ");
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
                
                if (inst instanceof Push)
                {
                    if (invalidstate == false)
                    {
                        Push push = (Push) inst;
                        stack.push(new StackValue(Integer.parseInt(push.n)));
                        // System.out.println("pushed " + push.n);
                    }
                }
                else if (inst instanceof Add)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                        stack.push(new StackValue(z1.intval + z2.intval));
                    }
                    
                    // System.out.println("add");
                    
                    
                }
                else if (inst instanceof Sub)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                    // System.out.println("sub");
                    
                    stack.push(new StackValue(z1.intval - z2.intval));
                    }
                }
                else if (inst instanceof Mult)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                    // System.out.println("mult");
                    
                    stack.push(new StackValue(z1.intval * z2.intval));
                    }
                }
                else if (inst instanceof Div)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                        // System.out.println("div");sss
                        if (z2.intval == 0)
                        {
                            invalidstate = true;
                        }
                        else
                        {
                            stack.push(new StackValue(z1.intval / z2.intval));
                        }
                    }
                }
                else if (inst instanceof Eq)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                        // System.out.println("eq");

                        stack.push(new StackValue(z1.intval.equals(z2.intval)));
                    }
                }
                else if (inst instanceof Le)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                        // System.out.println("le");
                        stack.push(new StackValue(z1.intval <= z2.intval));
                    }
                }
                else if (inst instanceof And)
                {
                    if (invalidstate == false)
                    {
                        StackValue z1 = stack.pop();
                        StackValue z2 = stack.pop();
                        // System.out.println("and");
                        if  (z1.boolval == true && z2.boolval == true)
                        {
                            stack.push(new StackValue(true));
                        }
                        else
                        {
                            stack.push(new StackValue(false));
                        }
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
                    if (invalidstate == false)
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
                    if (invalidstate == false)
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
                    if (invalidstate == false)
                    {
                        stack.push(new StackValue(true));
                        // System.out.println("pushed tt");
                    }
                }
                else if (inst instanceof False)
                {
                    if (invalidstate == false)
                    {
                        stack.push(new StackValue(false));
                        // System.out.println("pushed ff");
                    }
                }
                else if (inst instanceof Neg)
                {
                    if (invalidstate == false)
                    {
                        StackValue val = stack.pop();
                        if (val.boolval == true)
                        {
                            stack.push(new StackValue(false));
                            // System.out.println("pushed ff");
                        }
                        else
                        {
                            stack.push(new StackValue(true));
                            // System.out.println("pushed tt");
                        }
                    }
                    
                }
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
                else if (inst instanceof Store)
                {
                    if (invalidstate == false)
                    {
                        Store storeinst = (Store) inst;
                        StackValue val = stack.pop();
                        // System.out.println("stored " + val.intval);
                        store.put(storeinst.x, val.intval);
                    }
                }
                else if (inst instanceof Fetch)
                {
                    if (invalidstate == false)
                    {
                        Fetch fetch = (Fetch) inst;
                        stack.push(new StackValue(store.get(fetch.x)));
                        // System.out.println("fetched " + fetch.x + " with value " + store.get(fetch.x));
                    }
                }
                else
                {
                    System.out.println("Unknown instruction!!!!!!!!!! " + inst.getClass().getName());  
                }
            }
        System.out.println(store);
        return "Execution Done";
    }    
    
    private LinkedList<StackValue> stack;
    private HashMap<String, Integer> store;
    
    private class StackValue
    {
        public StackValue(int intval)
        {
            this.intval = intval;
            this.boolval = null;
        }
        
        public StackValue(boolean boolval)
        {
            this.intval = null;
            this.boolval = boolval;
        }
        
        public String toString() 
        { 
            return (boolval == null) ? intval.toString() : boolval.toString(); 
        }
        
        public final Integer intval;
        public final Boolean boolval;
    }
}