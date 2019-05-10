package semant;

import semant.amsyntax.*;

import semant.whilesyntax.Stm;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws Exception {
        Boolean step = false;
        int n = args.length;
        Stm s = WhileParser.parse(args[0]);
        if (n == 2 && args[1].equals("s"))
        {
            step = true;
        }

        s.accept(new ControlPointAssigner());
        System.out.println("while: ");
        s.accept(new PrettyPrinter());
        System.out.println("");
        System.out.println("");
        System.out.println("AM: ");
        System.out.println("");
        LinkedList<Inst> amcode = new LinkedList<Inst>();
        Code code = new Code();
        code = (s.accept(new ASTtoAM()));
        // System.out.println("this is code " + code);
        for (int i = 0; i < code.size(); i++)
        {
            amcode.addLast(code.get(i));
        }
        VM vm = new VM();
        AIVM aivm = new AIVM();
        // System.out.println("this is amcode " + amcode);
        System.out.println(vm.Execute(amcode, step));
        System.out.println(aivm.Execute(amcode, step));


    }
}
