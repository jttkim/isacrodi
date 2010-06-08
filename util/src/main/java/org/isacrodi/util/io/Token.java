package org.isacrodi.util.io;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * Immutable class for representing tokens.
 */
public class Token
{
  public enum TokenType
  {
    NAMEVALUE, BLOCKIDENTIFIER, SYMBOL
  }


  private TokenType tokenType;
  private String name;
  private String value;


  public Token(TokenType tokenType, String name, String value)
  {
    this.tokenType = tokenType;
    this.name = name;
    this.value = value;
  }


  public Token(TokenType tokenType, String name)
  {
    this(tokenType, name, null);
  }


  public TokenType getTokenType()
  {
    return (this.tokenType);
  }


  public String getName()
  {
    return (this.name);
  }


  public String getValue()
  {
    // FIXME: consider whether this should throw an exception fi there can't be a value
    return (this.value);
  }


  public boolean equals(Object other)
  {
    if ((other == null) || !(other instanceof Token))
    {
      return (false);
    }
    Token otherToken = (Token) other;
    if (this.tokenType != otherToken.tokenType)
    {
      return (false);
    }
    if ((this.name != null) && (otherToken.name == null))
    {
      return (false);
    }
    if ((this.name == null) && (otherToken.name != null))
    {
      return (false);
    }
    if ((this.name != null) && (otherToken.name != null) && !this.name.equals(otherToken.name))
    {
      return (false);
    }
    if ((this.value != null) && (otherToken.value == null))
    {
      return (false);
    }
    if ((this.value == null) && (otherToken.value != null))
    {
      return (false);
    }
    if ((this.value != null) && (otherToken.value != null) && !this.value.equals(otherToken.value))
    {
      return (false);
    }
    return (true);
  }


  public String toString()
  {
    String s;
    switch (this.tokenType)
    {
    case SYMBOL:
      s = String.format("symbol %s", this.name);
      break;
    case BLOCKIDENTIFIER:
      s = String.format("blockidentifier", this.name);
      break;
    case NAMEVALUE:
      s = String.format("namevalue (%s: %s)", this.name, this.value);
      break;
    default:
      s = "unknownToken";
      break;
    }
    return (s);
  }
}
