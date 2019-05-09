package semant;

import semant.amsyntax.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collection;

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
                
                
                
                System.out.print("\n        stack: ");
                for (int i = 0; i < stack.size(); i++)
                {
                
                    System.out.print(stack.get(i) + " ");
                }
                System.out.println("");
                System.out.println("        store: " + store);
                
                
                
                // System.out.println("");
                
                
                
                Inst inst = code.pop();
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
                    Push push = (Push) inst;
                    stack.push(new StackValue(Integer.parseInt(push.n)));
                    // System.out.println("pushed " + push.n);
                
                }
                else if (inst instanceof Add)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    {
                        stack.push(new StackValue(z1.intval + z2.intval));
                    }
                }
                
                else if (inst instanceof Sub)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    {
                        
                    stack.push(new StackValue(z1.intval - z2.intval));
                    }
                }
                else if (inst instanceof Mult)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    {    
                        // System.out.println("mult");
                        stack.push(new StackValue(z1.intval * z2.intval));
                    }
                }
                else if (inst instanceof Div)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    {    
                        // System.out.println("div");
                        if (z2.intval == 0)
                        {
                            stack.push(new StackValue(new StackValue.Bottom()));
                        }
                        else
                        {
                            stack.push(new StackValue(z1.intval / z2.intval));
                        }
                    }
                }
                else if (inst instanceof Eq)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    { 
                        // System.out.println("eq");

                        stack.push(new StackValue(z1.intval.equals(z2.intval)));
                    }
                }
                else if (inst instanceof Le)
                {
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    { 
                        // System.out.println("le");
                        stack.push(new StackValue(z1.intval <= z2.intval));
                    }
                }
                else if (inst instanceof And)
                {                    
                    StackValue z1 = stack.pop();
                    StackValue z2 = stack.pop();
                    if (z1.bottomval != null || z2.bottomval != null)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    { 
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
                        
                        if (z1.bottomval != null)
                        {
                            invalidstate = true;
                        }
                        else
                        { 
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
                    System.out.println("invalidstate is " + invalidstate);
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
                    System.out.println("invalidstate is " + invalidstate);
                    if (invalidstate == true)
                    {
                        invalidstate = false;
                    }
                }
                else if (inst instanceof Store)
                {
                    Store storeinst = (Store) inst;
                    StackValue val = stack.pop();
                    if (val.bottomval != null)
                    {
                        invalidstate = true;
                        // stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
                    { 
                        System.out.println("stored " + val.intval);
                        store.put(storeinst.x, val.intval);
                    }
                }
                else if (inst instanceof Fetch)
                {
                    if (invalidstate == true)
                    {
                        stack.push(new StackValue(new StackValue.Bottom()));
                    }
                    else
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
    
    public static class StackValue
    {
    
        public Bottom bottomval = null;
        
        public StackValue(int intval)
        {
            this.intval = intval;
            this.boolval = null;
            this.bottomval = null;
        }
        
        public StackValue(boolean boolval)
        {
            this.intval = null;
            this.boolval = boolval;
            this.bottomval = null;
        }
        
        public StackValue(Bottom b) 
        {
            this.intval = null;
            this.boolval = null;
            this.bottomval = b;
        } 
        
        public static class Bottom 
        {
            public String toString() { return "⊥"; }
        }
        
        public String toString() 
        { 
            if (bottomval != null)
            {
                return "⊥";
            }
            else
            {
                return (boolval == null) ? intval.toString() : boolval.toString(); 
            }
        }
        
        public final Integer intval;
        public final Boolean boolval;
    }
}