//Oliver Perez
//
//Class for a fraction
import java.math.BigInteger;

public class Fraction
{
    private int numerator;
    private int denominator;

    public Fraction(int numerator, int denominator)
    {
        if(denominator == 0)
            throw new ArithmeticException("Denominator cannot be zero.");
        if(denominator < 0)
        {
            numerator = -numerator;
            denominator = -denominator;
        }
        //Simplify fraction
        int gcd = BigInteger.valueOf(Math.abs(numerator)).gcd(BigInteger.valueOf(denominator)).intValue();
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public Fraction(int wholeNumber)
    {
        this(wholeNumber, 1);
    }

    public boolean isNegative()
    {
        return numerator < 0;
    }

    public boolean isZero()
    {
        return numerator == 0;
    }

    public int getNumerator()
    {
        return numerator;
    }

    public int getDenominator()
    {
        return denominator;
    }

    //Returns the result of 2 fractions added
    public Fraction add(Fraction other)
    {
        int n = this.numerator * other.denominator + other.numerator * this.denominator;
        int d = this.denominator * other.denominator;
        return new Fraction(n, d);
    }

    //Returns result of a fraction divided by an int
    public Fraction divideByInt(int k)
    {
        if(k == 0)
            throw new ArithmeticException("Divide by zero");
        return new Fraction(this.numerator, this.denominator * k);
    }

    //Returns result of a fraction * anotherFraction
    public Fraction multiplyBy(Fraction num)
    {
        return new Fraction(this.numerator * num.numerator, this.denominator * num.denominator);
    }

    //To string method for fraction
    @Override
    public String toString()
    {
        return numerator + "/" + denominator;
    }

    public double toDouble()
    {
        return numerator / (double) denominator;
    }
}
