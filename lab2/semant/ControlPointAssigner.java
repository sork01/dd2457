package semant;

import semant.amsyntax.Code;
import semant.whilesyntax.*;

public class ControlPointAssigner implements WhileVisitor {

    public int counter = 0;


    public Code visit(Conjunction and) {
        and.b1.accept(this);
        and.b2.accept(this);
        return null;
    }

    public Code visit(Assignment assignment) {
        counter++;
        assignment.controlPoint = counter;
        assignment.x.accept(this);
        assignment.a.accept(this);
        return null;
    }

    public Code visit(Compound compound) {
        compound.s1.accept(this);
        compound.s2.accept(this);
        return null;
    }

    public Code visit(Conditional conditional) {
        counter++;
        conditional.controlPoint = counter;
        conditional.b.accept(this);
        conditional.s1.accept(this);
        conditional.s2.accept(this);
        return null;
    }

    public Code visit(Equals equals) {
        equals.a1.accept(this);
        equals.a2.accept(this);
        return null;
    }

    public Code visit(FalseConst f) {
        return null;
    }

    public Code visit(LessThanEq leq) {
        leq.a1.accept(this);
        leq.a2.accept(this);
        return null;
    }

    public Code visit(Minus minus) {
        minus.a1.accept(this);
        minus.a2.accept(this);
        return null;
    }

    public Code visit(Not not) {
        not.b.accept(this);
        return null;
    }

    public Code visit(Num num) {
        return null;
    }

    public Code visit(Plus plus) {
        plus.a1.accept(this);
        plus.a2.accept(this);
        return null;
    }

    public Code visit(Skip skip) {
        counter++;
        skip.controlPoint = counter;

        return null;
    }

    public Code visit(Times times) {
        times.a1.accept(this);
        times.a2.accept(this);
        return null;
    }

    public Code visit(TrueConst t) {
        return null;
    }

    public Code visit(Var var) {
        return null;
    }

    public Code visit(While whyle) {
        counter++;
        whyle.controlPoint = counter;
        whyle.b.accept(this);
        whyle.s.accept(this);
        return null;
    }

    public Code visit(Divide div) {
        div.a1.accept(this);
        div.a2.accept(this);
        return null;
    }

    public Code visit(TryCatch trycatch) {
        counter++;
        trycatch.controlPoint = counter;
        trycatch.s1.accept(this);
        trycatch.s2.accept(this);
        return null;
    }

}
