package com.stuffwithstuff.magpie;

import java.util.*;

public abstract class Parser {
  public Parser(Lexer lexer) {
    mLexer = lexer;
    mRead = new LinkedList<Token>();
    mConsumed = new LinkedList<Token>();
  }

  protected Token last(int offset) {
    return mConsumed.get(offset - 1);
  }

  protected Token current() {
    return lookAhead(1);
  }
  
  protected boolean lookAhead(TokenType... types) {
    // make sure all of the types match before we start consuming
    for (int i = 0; i < types.length; i++) {
      if (!lookAhead(i).getType().equals(types[i]))
        return false;
    }

    return true;
  }
  
  /**
   * Gets whether or not the next Token is of any of the given types.
   * @param  types The allowed types for the next Token.
   * @return       true if the token is one of the types, false otherwise.
   */
  protected boolean lookAheadAny(TokenType... types) {
    for (TokenType type : types) {
      if (lookAhead(type)) return true;
    }
    
    return false;
  }

  protected boolean match(TokenType... types) {
    if (!lookAhead(types)) return false;

    // consume the matched tokens
    for (int i = 0; i < types.length; i++) {
      mConsumed.add(0, mRead.remove(0));
    }
    
    return true;
  }

  protected Token consume(TokenType type) {
    if (match(type)) {
      return last(1);
    } else {
      throw new ParseException("Expected " + type + " and found " + lookAhead(0));
    }
  }

  private Token lookAhead(int distance) {
    // read in as many as needed
    while (distance >= mRead.size()) {
      mRead.add(mLexer.readToken());
    }

    // get the queued token
    return mRead.get(distance);
  }

  private final Lexer mLexer;

  private final List<Token> mRead;
  private final List<Token> mConsumed;
}