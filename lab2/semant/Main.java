package semant;

import semant.amsyntax.*;

import semant.whilesyntax.Stm;
import java.util.LinkedList;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Stm s = WhileParser.parse(args[0]);
        
        s.accept(new ControlPointAssigner());
        System.out.println("while: ");
        s.accept(new PrettyPrinter());
        System.out.println("\n");
        
        Code code = new Code();
        code = s.accept(new ASTtoAM());
        
        AIVM aivm = new AIVM();
        
        aivm.Execute(new LinkedList<Inst>(code));
    }
}
