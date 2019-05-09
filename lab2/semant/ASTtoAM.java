package semant;

import semant.amsyntax.*;
import semant.whilesyntax.*;

public class ASTtoAM implements WhileVisitor {

    private Code code;
            
    public ASTtoAM(){
        code = new Code();
    }
    
    public Code visit(Compound compound)
    {
        Code code = new Code();
        code.addAll(compound.s1.accept(this));
        code.addAll(compound.s2.accept(this));
        return code;
    }
    
    public Code visit(Not not)
    {
        Code code = new Code();
        code.addAll(not.b.accept(this));
        code.add(new Neg());
        return code;
    }
    
    public Code visit(Conjunction and)
    {
        Code code = new Code();
        code.addAll(and.b2.accept(this));
        code.addAll(and.b1.accept(this));
        code.add(new And());
        return code;
    }
    
    public Code visit(Assignment assignment)
    {
        Code code = new Code();
        code.addAll(assignment.a.accept(this));
        code.add(new Store(assignment.x.id));
        return code;
    }
    
    public Code visit(Conditional conditional)
    {
        Code code = new Code();
        code.addAll(conditional.b.accept(this));
        code.add(new Branch(conditional.s1.accept(this), conditional.s2.accept(this)));
        return code;
    }
    
    public Code visit(Equals equals)
    {
        code.addAll(equals.a2.accept(this));
        code.addAll(equals.a1.accept(this));
        code.add(new Eq());
        return code;
    }
    
    public Code visit(FalseConst f)
    {
        Code code = new Code();
        code.add(new False());
        return code;
    }
    
    public Code visit(LessThanEq lessthaneq)
    {
        Code code = new Code();
        code.addAll(lessthaneq.a2.accept(this));
        code.addAll(lessthaneq.a1.accept(this));
        code.add(new Le());
        return code;
    }
    
    public Code visit(Minus minus)
    {
        Code code = new Code();
        code.addAll(minus.a2.accept(this));
        code.addAll(minus.a1.accept(this));
        code.add(new Sub());
        return code;
    }
    
    public Code visit(Num num)
    {
        Code code = new Code();
        code.add(new Push(num.n));
        return code;
    }
    
    public Code visit(Plus plus)
    {
        Code code = new Code();
        code.addAll(plus.a2.accept(this));
        code.addAll(plus.a1.accept(this));
        code.add(new Add());
        return code;
    }
    
    public Code visit(Skip skip)
    {
        Code code = new Code();
        code.add(new Noop());
        return code;
    }
    
    public Code visit(Times times)
    {
        Code code = new Code();
        code.addAll(times.a2.accept(this));
        code.addAll(times.a1.accept(this));
        code.add(new Mult());
        return code;
    }
    
    public Code visit(TrueConst t)
    {
        Code code = new Code();
        code.add(new True());
        return code;
    }
    
    public Code visit(Var var)
    {
        Code code = new Code();
        code.add(new Fetch(var.id));
        return code;
    }
    
    public Code visit(While whyle)
    {
        Code code = new Code();
        code.add(new Loop(whyle.b.accept(this), whyle.s.accept(this)));
        return code;
    }
    
    public Code visit(TryCatch trycatch)
    {
        // System.err.println("testtrycatch");
        Code code = new Code();
        code.add(new AMTryCatch(trycatch.s1.accept(this), trycatch.s2.accept(this)));
        return code;
    }
    
    public Code visit(Divide div)
    {
        Code code = new Code();
        code.addAll(div.a2.accept(this));
        code.addAll(div.a1.accept(this));
        code.add(new Div());
        return code;
    }
    
    public String toString() 
    {
        String ans = "";
        for (int i = 0; i <= code.size() - 1; i++) 
        {
            // System.out.println("here?");
            ans += code.get(i) + " ";
        }
        return ans;
    }
    
    
    
}
