package lpg.runtime;

public class DeterministicParser extends Stacks
{
    private boolean taking_actions = false;
    private int markerKind = 0;
    
    private Monitor monitor;
    private int START_STATE,
                NUM_RULES,
                NT_OFFSET,
                LA_STATE_OFFSET,
                EOFT_SYMBOL,                
                ACCEPT_ACTION,
                ERROR_ACTION,
                ERROR_SYMBOL;
    
    private int lastToken,
                currentAction;
    private IntTuple action = null;

    private TokenStream tokStream;
    private ParseTable prs;
    private RuleAction ra;

    //
    // keep looking ahead until we compute a valid action
    //
    private int lookahead(int act, int token)
    {
        act = prs.lookAhead(act - LA_STATE_OFFSET, tokStream.getKind(token));
        return (act > LA_STATE_OFFSET
                    ? lookahead(act, tokStream.getNext(token))
                    : act);
    }
    
    //
    // Compute the next action defined on act and sym. If this
    // action requires more lookahead, these lookahead symbols
    // are in the token stream beginning at the next token that
    // is yielded by peek().
    //
    private int tAction(int act, int sym)
    {
        act = prs.tAction(act, sym);
        return (act > LA_STATE_OFFSET
                    ? lookahead(act, tokStream.peek())
                    : act);
    }

    //
    // Compute the next action defined on act and the next k tokens
    // whose types are stored in the array sym starting at location
    // index. The array sym is a circular buffer. If we reach the last
    // element of sym and we need more lookahead, we proceed to the
    // first element.
    // 
    // assert(sym.length == prs.getMaxLa());
    //
    private int tAction(int act, int sym[], int index)
    {
        act = prs.tAction(act, sym[index]);
        while(act > LA_STATE_OFFSET)
        {
            index = ((index + 1) % sym.length);
            act = prs.lookAhead(act - LA_STATE_OFFSET, sym[index]);
        }

        return act;
    }

    //
    // Process reductions and continue...
    //
    private final void processReductions()
    {
        do
        {
            stateStackTop -= (prs.rhs(currentAction) - 1);
            ra.ruleAction(currentAction);
            currentAction = prs.ntAction(stateStack[stateStackTop],
                                         prs.lhs(currentAction));
        } while(currentAction <= NUM_RULES);

        return;
    }
    
    //
    // The following functions can be invoked only when the parser is
    // processing actions. Thus, they can be invoked when the parser
    // was entered via the main entry point (parse()). When using
    // the incremental parser (via the entry point parse(int [], int)),
    // an Exception is thrown if any of these functions is invoked?
    // However, note that when parseActions() is invoked after successfully
    // parsing an input with the incremental parser, then they can be invoked.
    //
    public final int getCurrentRule()
    {
        if (taking_actions)
            return currentAction;
        throw new UnavailableParserInformationException();
    }
    public final int getFirstToken()
    {
        if (taking_actions)
            return getToken(1);
        throw new UnavailableParserInformationException();
    }
    public final int getFirstToken(int i)
    {
        if (taking_actions)
            return getToken(i);
        throw new UnavailableParserInformationException();
    }
    public final int getLastToken()
    {
        if (taking_actions)
            return lastToken;
        throw new UnavailableParserInformationException();
    }
    public final int getLastToken(int i)
    {
        if (taking_actions)
            return (i >= prs.rhs(currentAction)
                       ? lastToken
                       : tokStream.getPrevious(getToken(i + 1)));
        throw new UnavailableParserInformationException();
    }

    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    public void reset()
    {
        this.taking_actions = false;
        this.markerKind = 0;

        if (action != null) action.reset();
    }

    public void reset(Monitor monitor, TokenStream tokStream)
    {
        this.monitor = monitor;
        this.tokStream = (TokenStream) tokStream;

        reset();
    }
    
    public void reset(TokenStream tokStream)
    {
    	reset(null, tokStream);
    }
    
    public void reset(Monitor monitor, TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                                    NotDeterministicParseTableException
    {
    	reset(monitor, tokStream);

    	this.prs = prs;
        this.ra = ra;
        
        START_STATE = prs.getStartState();
        NUM_RULES = prs.getNumRules();
        NT_OFFSET = prs.getNtOffset();
        LA_STATE_OFFSET = prs.getLaStateOffset();
        EOFT_SYMBOL = prs.getEoftSymbol();
        ERROR_SYMBOL = prs.getErrorSymbol();
        ACCEPT_ACTION = prs.getAcceptAction();
        ERROR_ACTION = prs.getErrorAction();

        if (! prs.isValidForParser()) throw new BadParseSymFileException();
        if (prs.getBacktrack()) throw new NotDeterministicParseTableException();
    }

    public void reset(TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                   NotDeterministicParseTableException
    {
        reset(null, tokStream, prs, ra);
    }

    public DeterministicParser() {}
    
    public DeterministicParser(TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                            NotDeterministicParseTableException
    {
        reset(null, tokStream, prs, ra);
    }

    public DeterministicParser(Monitor monitor, TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                                             NotDeterministicParseTableException
    {
        reset(monitor, tokStream, prs, ra);
    }
    
    //
    //
    //
    public Object parse() throws BadParseException
    {
        return parseEntry(0);
    }

    //
    //
    //
    public Object parseEntry(int marker_kind) throws BadParseException
    {
        //
        // Indicate that we are running the regular parser and that it's
        // ok to use the utility functions to query the parser.
        //
        taking_actions = true;
        
        //
        // Reset the token stream and get the first token.
        //
        tokStream.reset();
        lastToken = tokStream.getPrevious(tokStream.peek());
        int curtok,
            current_kind;
        if (marker_kind == 0)
        {
            curtok = tokStream.getToken();
            current_kind = tokStream.getKind(curtok);
        }
        else
        {
            curtok = lastToken;
            current_kind = marker_kind;
        }
        
        //
        // Start parsing.
        //
        reallocateStacks(); // make initial allocation
        stateStackTop = -1;
        currentAction = START_STATE;
        
        ProcessTerminals: for (;;)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
            {
                taking_actions = false; // indicate that we are done
                return null;
            }

            try
            {
                stateStack[++stateStackTop] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                stateStack[stateStackTop] = currentAction;
            }

            locationStack[stateStackTop] = curtok;

            currentAction = tAction(currentAction, current_kind);
 
            if (currentAction <= NUM_RULES)
            {
                stateStackTop--; // make reduction look like a shift-reduce
                processReductions();
            }
            else if (currentAction > ERROR_ACTION)
            {
                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                currentAction -= ERROR_ACTION;
                processReductions();
            }
            else if (currentAction < ACCEPT_ACTION)
            {
                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
            }
            else break ProcessTerminals;
        }

        taking_actions = false; // indicate that we are done

        if (currentAction == ERROR_ACTION)
            throw new BadParseException(curtok);

        return parseStack[marker_kind == 0 ? 0 : 1];
    }

    //
    // This method is invoked when using the parser in an incremental mode
    // using the entry point parse(int [], int).
    //
    public void resetParser()
    {
        resetParserEntry(0);
    }
    
    //
    // This method is invoked when using the parser in an incremental mode
    // using the entry point parse(int [], int).
    //
    public void resetParserEntry(int marker_kind)
    {
        this.markerKind = marker_kind;

        if (stateStack == null)
            reallocateStacks(); // make initial allocation
        stateStackTop = 0;
        stateStack[stateStackTop] = START_STATE;
        if (action == null)
             action = new IntTuple(1 << 20);
        else action.reset();

        //
        // Indicate that we are going to run the incremental parser and that
        // it's forbidden to use the utility functions to query the parser.
        //
        taking_actions = false;

        if (marker_kind != 0)
        {
            int sym[] = new int[1];
            sym[0] = markerKind;
            parse(sym, 0);
        }
    }

    //
    // Find a state in the state stack that has a valid action on ERROR token
    //
    private boolean recoverableState(int state)
    {
        for (int k = prs.asi(state); prs.asr(k) != 0; k++)
        {
           if (prs.asr(k) == ERROR_SYMBOL)
                return true;
        }
        return false;
    }

    //
    // Reset the parser at a point where it can legally process
    // the error token. If we can't do that, reset it to the beginning.
    //
    public void errorReset()
    {
        int gate = (this.markerKind == 0 ? 0 : 1);
        for (; stateStackTop >= gate; stateStackTop--)
            if (recoverableState(stateStack[stateStackTop]))
                break;
        if (stateStackTop < gate)
            resetParserEntry(markerKind);
        return;
    }

    //
    // This is an incremental LALR(k) parser that takes as argument
    // the next k tokens in the input. If these k tokens are valid for
    // the current configuration, it advances past the first of the k
    // tokens and returns either:
    //
    //    . the last transition induced by that token 
    //    . the Accept action
    //
    // If the tokens are not valid, the initial configuration remains
    // unchanged and the Error action is returned.
    //
    // Note that it is the user's responsibility to start the parser in a
    // proper configuration by initially invoking the method resetParser
    // prior to invoking this function.
    //
    public int parse(int sym[], int index)
    {
        // assert(sym.length == prs.getMaxLa());
        
        //
        // First, we save the current length of the action tuple, in
        // case an error is encountered and we need to restore the
        // original configuration.
        //
        // Next, we declara and initialize the variable pos which will
        // be used to indicate the highest useful position in stateStack
        // as we are simulating the actions induced by the next k input
        // terminals in sym.
        //
        // The location stack will be used here as a temporary stack
        // to simulate these actions. We initialize its first useful
        // offset here.
        //
        int save_action_length = action.size(),
            pos = stateStackTop,
            location_top = stateStackTop - 1;

        //
        // When a reduce action is encountered, we compute all REDUCE
        // and associated goto actions induced by the current token.
        // Eventually, a SHIFT, SHIFT-REDUCE, ACCEPT or ERROR action is
        // computed...
        //
        for (currentAction = tAction(stateStack[stateStackTop], sym, index);
             currentAction <= NUM_RULES;
             currentAction = tAction(currentAction, sym, index))
        {
            action.add(currentAction);
            do
            {
                location_top -= (prs.rhs(currentAction) - 1);
                int state = (location_top > pos
                                          ? locationStack[location_top]
                                          : stateStack[location_top]);
                currentAction = prs.ntAction(state, prs.lhs(currentAction));
            } while (currentAction <= NUM_RULES);
            
            //
            // ... Update the maximum useful position of the
            // stateSTACK, push goto state into stack, and
            // continue by compute next action on current symbol
            // and reentering the loop...
            //
            pos = pos < location_top ? pos : location_top;
            try
            {
                locationStack[location_top + 1] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                locationStack[location_top + 1] = currentAction;
            }
        }

        //
        // At this point, we have a shift, shift-reduce, accept or error
        // action. stateSTACK contains the configuration of the state stack
        // prior to executing any action on the currenttoken. locationStack
        // contains the configuration of the state stack after executing all
        // reduce actions induced by the current token. The variable pos
        // indicates the highest position in the stateSTACK that is still
        // useful after the reductions are executed.
        //
        if (currentAction > ERROR_ACTION || // SHIFT-REDUCE action ?
            currentAction < ACCEPT_ACTION)  // SHIFT action ?
        {
            action.add(currentAction);
            //
            // If no error was detected, update the state stack with 
            // the info that was temporarily computed in the locationStack.
            //
            stateStackTop = location_top + 1;
            for (int i = pos + 1; i <= stateStackTop; i++)
                stateStack[i] = locationStack[i];

            //
            // If we have a shift-reduce, process it as well as
            // the goto-reduce actions that follow it.
            //
            if (currentAction > ERROR_ACTION)
            {
                currentAction -= ERROR_ACTION;
                do
                {
                    stateStackTop -= (prs.rhs(currentAction) - 1);
                    currentAction = prs.ntAction(stateStack[stateStackTop],
                                                 prs.lhs(currentAction));
                } while (currentAction <= NUM_RULES);
            }

            //
            // Process the final transition - either a shift action of
            // if we started out with a shift-reduce, the final GOTO
            // action that follows it.
            //
            try
            {
                stateStack[++stateStackTop] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                stateStack[stateStackTop] = currentAction;
            }
        }
        else if (currentAction == ERROR_ACTION)
             action.reset(save_action_length); // restore original action state.
        return currentAction;
    }

    //
    // Now do the final parse of the input based on the actions in
    // the list "action" and the sequence of tokens in the token stream.
    //
    public Object parseActions() throws BadParseException
    {
        //
        // Indicate that we are processing actions now (for the incremental
        // parser) and that it's ok to use the utility functions to query the
        // parser.
        //
        taking_actions = true;
        
        tokStream.reset();
        lastToken = tokStream.getPrevious(tokStream.peek());
        int curtok = (markerKind == 0 ? tokStream.getToken() : lastToken);

        try
        {
            //
            // Reparse the input...
            //
            stateStackTop = -1;
            currentAction = START_STATE;

            for (int i = 0; i < action.size(); i++)
            {
                //
                // if the parser needs to stop processing, it may do so here.
                //
                if (monitor != null && monitor.isCancelled())
                {
                    taking_actions = false; // indicate that we are done
                    return null;
                }

                stateStack[++stateStackTop] = currentAction;
                locationStack[stateStackTop] = curtok;

                currentAction = action.get(i);
                if (currentAction <= NUM_RULES) // a reduce action?
                {
                    stateStackTop--; // turn reduction intoshift-reduction
                    processReductions();
                }
                else // a shift or shift-reduce action
                {
                    lastToken = curtok;
                    curtok = tokStream.getToken();
                    if (currentAction > ERROR_ACTION) // a shift-reduce action?
                    {
                        currentAction -= ERROR_ACTION;
                        processReductions();
                    }
                }
            }
        }
        catch (Throwable e) // if any exception is thrown, indicate BadParse
        {
            taking_actions = false; // indicate that we are done.
            throw new BadParseException(curtok);
        }

        taking_actions = false; // indicate that we are done.
        action = null; // turn into garbage
        return parseStack[markerKind == 0 ? 0 : 1];
    }
}
