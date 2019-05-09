package semant.amsyntax;


public abstract class Inst {
    
    public enum Opcode { ADD, AND, BRANCH, CHECK, DIV, EQ, FALSE, FETCH, LE,
        LOOP, MULT, NEG, NOOP, PUSH, STORE, SUB, TRUE, TRYCATCH, UNCHECK
    };
    
    
    public final Opcode opcode;
    
    
    public Inst(Opcode opcode) {
        this.opcode = opcode;
    }
    
    
    public String toString() {
        return opcode.toString();
    }

    int stmControlPoint;
    
    
    // Two instructions are equal iff their string representations are equal.
    public boolean equals(Object o) {
        if (!(o instanceof Inst))
            return false;
        
        return o.toString().equals(toString());
    }
    
    
    public int hashCode() {
        return toString().hashCode();
    }
    
}
