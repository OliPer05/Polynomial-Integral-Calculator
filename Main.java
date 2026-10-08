//Oliver Perez
//
import java.util.*;
import java.io.*;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        //Ask for filename and store
        System.out.print("Enter file name: ");
        String fileName = scanner.next();
        scanner.close();

        //Try to open file
        Scanner iFile;
        try
        {
            iFile = new Scanner(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File could not be opened.");
            return;
        }

        //Run for each line of the file
        while(iFile.hasNextLine())
        {
            String line = iFile.nextLine();
            if(line == null || line.trim().isEmpty())
                continue;
            
            //get header a|b and expresion equation
            //header [0] expression [1]
            String[] headerAndExpr = parseHeaderAndExpression(line);

            //Get bounds if definite
            Optional<int[]> ifDefiniteBounds = tryParseBounds(headerAndExpr[0]);

            //Get arrraylist of term strings
            ArrayList<String> termStrings = parseIntegralExpression(headerAndExpr[1]);

            //Create tree 
            BinTree<Term> tree = new BinTree<>(
            (a, b) -> new Term(
                a.getCoefficient().add(b.getCoefficient()), // add coefficients
                a.getExponent()                               // exponents are equal when compareTo==0
            ));
            


            //Go through arraylist, parse term, and add to tree
            for(String ts : termStrings)
            {
                Term t = parseTermString(ts);
                tree.insert(t);
            }

            //Integrate tree and display indefinite array
            BinTree<Term> antiDerTree = integrateTree(tree);
            
            //if indefinate display antiderivative
            if(!ifDefiniteBounds.isPresent())
            {
                displayIndefinite(antiDerTree);
                continue;       //Skip rest of while go to next line
            }

            //integral is definite
            int[] bounds = ifDefiniteBounds.get();

            //Evaluate at upper bound
            double integralVal = evaluateIntegralAt(antiDerTree, bounds[1]);

            //Evaluate at lower bound
            integralVal -= evaluateIntegralAt(antiDerTree, bounds[0]);

            //display definite integral
            displayDefinite(antiDerTree, bounds, integralVal);
        }
        iFile.close();
    }

    //Evaluate at bound
    private static double evaluateIntegralAt(BinTree<Term> antiDerTree, int bound)
    {
        ArrayList<Term> antiDerList = antiDerTree.toListDescending();
        double val = 0;
        for(Term trm : antiDerList)
        {
            val += trm.evaluateAt(bound);
        }
        return val;
    }

    //Function to parse term string
    private static Term parseTermString(String termString)
    {
        termString = termString.trim();
        if(termString.isEmpty())
        {
            return new Term(new Fraction(0,1), 0);
        }

        int sign = 1;
        if(termString.startsWith("+"))
        {
            termString = termString.substring(1).trim();
        }
        else if(termString.startsWith("-"))
        {
            sign = -1;
            termString = termString.substring(1).trim();
        }

        if(termString.isEmpty())
        {
            return new Term(new Fraction(0,1), 0);
        }

        int xPos = termString.indexOf('x');
        if(xPos == -1)
        {
            if(termString.isEmpty())
            {
                return new Term(new Fraction(0,1), 0);
            }
            try
            {
                int coeff = Integer.parseInt(termString);
                return new Term(new Fraction(sign * coeff, 1), 0);
            }
            catch(NumberFormatException e)
            {
                return new Term(new Fraction(0,1), 0);
            }
        }

        String coefPart = termString.substring(0, xPos).trim();
        int coeff;
        if(coefPart.isEmpty() || coefPart.equals("+"))
        {
            coeff = 1;
        }
        else if(coefPart.equals("-"))
        {
            coeff = -1;
        }
        else
        {
            try
            {
                coeff = Integer.parseInt(coefPart);
            }
            catch(NumberFormatException e)
            {
                return new Term(new Fraction(0,1), 0);
            }
        }

        int exp = 1;
        int caret = termString.indexOf('^', xPos);
        if(caret != -1)
        {
            String expStr = termString.substring(caret + 1).trim();
            if(!expStr.isEmpty())
            {
                try
                {
                    exp = Integer.parseInt(expStr);
                }
                catch(NumberFormatException e)
                {
                    exp = 1;
                }
            }
            else
            {
                exp = 1;
            }
        }

        return new Term(new Fraction(sign * coeff, 1), exp);
    }

    //Will seperate the header and the expression header '|' and the rest of the expresion being what is going to be integrated
    private static String[] parseHeaderAndExpression(String integralLine)
    {
        String header = integralLine.substring(0, integralLine.indexOf(' '));
        String expression = integralLine.substring(integralLine.indexOf(' ') + 1, integralLine.indexOf("dx") - 1);
        return new String[] {header, expression};
    }

    // Will parse the header of the integral line being where the '|' is at and return bounds or none if not found
    private static Optional<int[]> tryParseBounds(String header)    
    {
        if(header.equals("|"))
        {
            return Optional.empty();
        }
        int lowerBound = Integer.parseInt(header.substring(0, header.indexOf('|')));
        int upperBound = Integer.parseInt(header.substring(header.indexOf('|') + 1));
        return Optional.of(new int[] {lowerBound, upperBound});
    }

    //Will parse the expression string
    private static ArrayList<String> parseIntegralExpression(String expr)
    {
        expr = expr.replaceAll("\\s+", "");

        if(expr.length() > 0 && expr.charAt(0) != '-' && expr.charAt(0) != '+')
        {
            expr = "+" + expr;
        }

        ArrayList<String> parts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            if((c == '+' || c == '-') && !(i > 0 && expr.charAt(i - 1) == '^'))
            {
                if(sb.length() > 0)
                {
                    parts.add(sb.toString());
                }
                sb.setLength(0);
                sb.append(c);
            }
            else
            {
                sb.append(c);
            }
        }

        if(sb.length() > 0)
        {
            parts.add(sb.toString());
        }

        return parts;
    }


    //From core implementation Not used anymore in final submission deos not work
    //Function to parse integral line returns an arraylist of string terms
    private static ArrayList<String> parseIntegralLine(String line)
    {
        int dxIdx = line.lastIndexOf("dx");
        String expr = (dxIdx != -1) ? line.substring(0, dxIdx) : line;

        if(expr.contains("|"))
        {
            expr = expr.substring(expr.indexOf('|') + 1);
        }

        expr = expr.replaceAll("\\s+", "");

        if(expr.length() > 0 && expr.charAt(0) != '-' && expr.charAt(0) != '+')
        {
            expr = "+" + expr;
        }

        ArrayList<String> parts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            if((c == '+' || c == '-') && !(i > 0 && expr.charAt(i - 1) == '^'))
            {
                if(sb.length() > 0)
                {
                    parts.add(sb.toString());
                }
                sb.setLength(0);
                sb.append(c);
            }
            else
            {
                sb.append(c);
            }
        }

        if(sb.length() > 0)
        {
            parts.add(sb.toString());
        }

        return parts;
    }

    //Function that integrates evry term on the tree
    private static BinTree<Term> integrateTree(BinTree<Term> bsTree)
    {
        BinTree<Term> out = new BinTree<>(
        (a, b) -> new Term(a.getCoefficient().add(b.getCoefficient()), a.getExponent())
        );
        for(Term t : bsTree.toListDescending())
        {
            Term it = t.integrate();
            out.insert(it);
        }
        return out;
    }

    //Function to display definite integral
    private static void displayDefinite(BinTree<Term> tree, int[] bounds, double integralVal)
    {
        ArrayList<Term> terms = tree.toListDescending();
        StringBuilder out = new StringBuilder();
        boolean printedAny = false;

        for (Term t : terms)
        {
            Fraction c = t.getCoefficient();
            if (c.isZero())
            {
                continue;
            }

            boolean isFrac = (c.getDenominator() != 1);
            boolean neg = (c.getNumerator() < 0);

            // Absolute term
            Term absTerm = new Term(
                new Fraction(Math.abs(c.getNumerator()), c.getDenominator()),
                t.getExponent()
            );
            String absStr = absTerm.toString();

            if (isFrac && neg)
            {
                if (absStr.length() > 1 && absStr.charAt(0) == '(' && absStr.charAt(1) != '-')
                {
                    absStr = "(" + absStr.substring(1);
                }
            }

            if (!printedAny)
            {
                if(isFrac && neg)
                {
                    out.append(t.toString());
                }
                else if (neg)
                {
                    out.append("-").append(absStr);
                }
                else
                {
                    out.append(absStr);
                }
            }
            else
            {
                if (neg)
                {
                    out.append(" - ").append(absStr);
                }
                else
                {
                    out.append(" + ").append(absStr);
                }
            }

            printedAny = true;
        }

        if (!printedAny)
        {
            out.append("0");
        }

        // Append bounds and 3-decimal value
        out.append(", ").append(bounds[0]).append("|").append(bounds[1])
        .append(" = ").append(String.format("%.3f", integralVal));

        System.out.println(out.toString());
    }

    
    //Function to display indefinite integral
    private static void displayIndefinite(BinTree<Term> tree)
    {
        ArrayList<Term> terms = tree.toListDescending();
        StringBuilder out = new StringBuilder();
        boolean printedAny = false;

        for (Term t : terms)
        {
            Fraction c = t.getCoefficient();
            if (c.isZero())
            {
                continue;
            }

            boolean isFrac = (c.getDenominator() != 1);
            boolean neg = (c.getNumerator() < 0);

            Term absTerm = new Term(
                new Fraction(Math.abs(c.getNumerator()), c.getDenominator()),
                t.getExponent()
            );
            String absStr = absTerm.toString();

            if (isFrac && neg)
            {
                if (absStr.length() > 1 && absStr.charAt(0) == '(' && absStr.charAt(1) != '-')
                {
                    absStr = "(" + absStr.substring(1);
                }
            }

            if (!printedAny)
            {
                if(isFrac && neg)
                {
                    out.append(t.toString());
                }
                else if (neg)
                {
                    out.append("-").append(absStr);
                }
                else
                {
                    out.append(absStr);
                }
            }
            else
            {
                // " + " or " - " then the absolute term
                if (neg)
                {
                    out.append(" - ").append(absStr);
                }
                else
                {
                    out.append(" + ").append(absStr);
                }
            }

            printedAny = true;
        }

        if (!printedAny)
        {
            out.append("0");
        }

        out.append(" + C");
        System.out.println(out.toString());
    }
}
