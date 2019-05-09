package semant;

import semant.amsyntax.*;
import semant.whilesyntax.*;

public class CompileVisitor implements WhileVisitor {
    
    public Code visit(Compound compound) {
        Code c = new Code();
        c.addAll(compound.s1.accept(this));
        c.addAll(compound.s2.accept(this));
        return c;
    }
    
    public Code visit(Not not) {
        Code c = new Code();
        c.addAll(not.b.accept(this));
        c.add(new Neg());
        return c;
    }
    
    public Code visit(Conjunction and) {
        return null;
    }
    
    public Code visit(Assignment assignment) {
        return null;
    }
    
    public Code visit(Conditional conditional) {
        return null;
    }
    
    public Code visit(Equals equals) {
        return null;
    }

    public Code visit(FalseConst f) {
        return null;
    }

    public Code visit(LessThanEq lessthaneq) {
        return null;
    }

    public Code visit(Minus minus) {
        return null;
    }

    public Code visit(Num num) {
        return null;
    }
    
    public Code visit(Plus plus) {
        return null;
    }

    public Code visit(Skip skip) {
        return null;
    }

    public Code visit(Times times) {
        return null;
    }
    
    public Code visit(TrueConst t) {
        return null;
    }

    public Code visit(Var var) {
        return null;
    }

    public Code visit(While whyle) {
        return null;
    }
    
    public Code visit(TryCatch trycatch) {
        return null;
    }
    
    public Code visit(Divide div) {
        return null;
    }
}
