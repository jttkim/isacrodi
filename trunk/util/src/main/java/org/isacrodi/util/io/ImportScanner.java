package org.isacrodi.util.io;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * A quite primitve scanner for reading isacrodi data files.
 *
 * Each line contains a token. (Hence, curly braces must be on
 * separate lines.)
 */
public class ImportScanner
{
  private Pattern nameValuePattern;
  private Pattern blockIdentifierPattern;
  private Pattern singleSymbolPattern;
  private BufferedReader in;
  private int lineNumber;


  public ImportScanner(BufferedReader in)
  {
    this.in = in;
    this.lineNumber = 0;
    this.nameValuePattern = Pattern.compile("\\s*([A-Za-z][A-Za-z0-9]*)\\s*:\\s*(.*)");
    this.blockIdentifierPattern = Pattern.compile("\\s*([A-Za-z][A-Za-z0-9]*)\\s*");
    this.singleSymbolPattern = Pattern.compile("\\s*([{}])");
  }


  public ImportToken nextToken() throws IOException
  {
    String s = this.in.readLine();
    this.lineNumber++;
    while ((s != null) && s.trim().equals(""))
    {
      s = this.in.readLine();
      this.lineNumber++;
    }
    if (s == null)
    {
      return (null);
    }
    Matcher m = this.nameValuePattern.matcher(s);
    if (m.find())
    {
      return (new ImportToken(ImportToken.TokenType.NAMEVALUE, m.group(1), m.group(2)));
    }
    m =this.blockIdentifierPattern.matcher(s);
    if (m.find())
    {
      return (new ImportToken(ImportToken.TokenType.BLOCKIDENTIFIER, m.group(1)));
    }
    m = this.singleSymbolPattern.matcher(s);
    if (m.find())
    {
      return (new ImportToken(ImportToken.TokenType.SYMBOL, m.group(1)));
    }
    throw new IllegalStateException(String.format("line %d: unparsable content"));
  }


  public ImportToken nextToken(ImportToken.TokenType expectedTokenType) throws IOException
  {
    ImportToken t = this.nextToken();
    if (t.getTokenType() != expectedTokenType)
    {
      throw new IllegalStateException(String.format("line: %d: illegal token", this.lineNumber));
    }
    return (t);
  }


  public ImportToken nextToken(ImportToken.TokenType expectedTokenType, String expectedName) throws IOException
  {
    ImportToken t = this.nextToken(expectedTokenType);
    if (!t.getName().equals(expectedName))
    {
      throw new IllegalStateException(String.format("line %d: expected name %s but found %s", expectedName, t.getName()));
    }
    return (t);
  }
}
