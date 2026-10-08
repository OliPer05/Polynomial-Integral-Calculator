//Oliver Perez
//
//Class for a polynomial term
import java.lang.Math;

public class Term implements Comparable<Term>
{
    private Fraction coefficient;
    private int exponent;

    public Term(Fraction coefficient, int exponent)
    {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public Fraction getCoefficient()
    {
        return coefficient;
    }

    public int getExponent()
    {
        return exponent;
    }

    @Override
    public int compareTo(Term other)
    {
        return Integer.compare(this.exponent, other.exponent);
    }

    @Override
    public String toString()
    {
        int num = coefficient.getNumerator();
        int den = coefficient.getDenominator();
        boolean isFrac = den != 1;

        if(exponent == 0)
        {
            if(isFrac)
            {
                if(num < 0)
                {
                    return "(- " + Math.abs(num) + "/" + den + ")";
                }
                else
                {
                    return "(" + num + "/" + den + ")";
                }
            }
            else
            {
                return String.valueOf(Math.abs(num));
            }
        }

        String coefStr = "";
        if(isFrac)
        {
            if(num < 0)
            {
                coefStr = "(- " + Math.abs(num) + "/" + den + ")";
            }
            else
            {
                coefStr = "(" + num + "/" + den + ")";
            }
        }
        else
        {
            int absInt = Math.abs(num);
            if(absInt != 1)
            {
                coefStr = String.valueOf(absInt);
            }
        }

        String varExp;
        if(exponent == 1)
        {
            varExp = "x";
        }
        else
        {
            varExp = "x^" + exponent;
        }

        return (coefStr.isEmpty() ? "" : coefStr) + varExp;
    }

    public boolean isNegative()
    {
        return coefficient.isNegative();
    }

    //apply integration rule to term and return it
    public Term integrate()
    {
        int newExp = this.exponent + 1;
        Fraction newCoef = this.coefficient.divideByInt(newExp);
        return new Term(newCoef, newExp);
    }

    //Function evaluates a term at a value x
    public double evaluateAt(int x)
    {
        return coefficient.toDouble() * Math.pow(x, exponent);
    }
}
