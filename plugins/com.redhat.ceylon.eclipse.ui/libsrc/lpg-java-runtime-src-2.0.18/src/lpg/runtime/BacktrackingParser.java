package lpg.runtime;

public class BacktrackingParser extends Stacks
{
    private Monitor monitor;
    private int START_STATE,
                NUM_RULES,
                NT_OFFSET,
                LA_STATE_OFFSET,
                EOFT_SYMBOL,
                ERROR_SYMBOL,
                ACCEPT_ACTION,
                ERROR_ACTION;

    private int lastToken,
                currentAction;
    private TokenStream tokStream;
    private ParseTable prs;
    private RuleAction ra;
    private IntSegmentedTuple action = new IntSegmentedTuple(10, 1024); // IntTuple(1 << 20),
    private IntTuple tokens;
    private int actionStack[];
    private boolean skipTokens = false; // true if error productions are used to skip tokens
    
    //
    // A starting marker indicates that we are dealing with an entry point
    // for a given nonterminal. We need to execute a shift action on the
    // marker in order to parse the entry point in question.
    //
    private int markerTokenIndex = 0;
    private int getMarkerToken(int marker_kind, int start_token_index)        
    {
        if (marker_kind == 0)
            return 0;
        else
        {
            if (markerTokenIndex == 0)
            { 
                if (! (tokStream instanceof IPrsStream))
                    throw new TokenStreamNotIPrsStreamException();
                markerTokenIndex = ((IPrsStream) tokStream).makeErrorToken(tokStream.getPrevious(start_token_index),
                                                                           tokStream.getPrevious(start_token_index),
                                                                           tokStream.getPrevious(start_token_index),
                                                                           marker_kind);
            }
            else ((IPrsStream) tokStream).getIToken(markerTokenIndex).setKind(marker_kind);
        }
        
        return markerTokenIndex;
    }

    //
    // Override the getToken function in Stacks.
    //
    public final int getToken(int i)
    {
        return tokens.get(locationStack[stateStackTop + (i - 1)]);
    }

    public final int getCurrentRule()     { return currentAction; }
    public final int getFirstToken()      { return tokStream.getFirstRealToken(getToken(1)); }
    public final int getFirstToken(int i) { return tokStream.getFirstRealToken(getToken(i)); }
    public final int getLastToken()       { return tokStream.getLastRealToken(lastToken); }
    public final int getLastToken(int i)  { int l = (i >= prs.rhs(currentAction)
                                                        ? lastToken
                                                        : tokens.get(locationStack[stateStackTop + i] - 1));
                                            return tokStream.getLastRealToken(l);
                                          }

    public void setMonitor(Monitor monitor) { this.monitor = monitor; }
    
    public void reset()
    {
    	action.reset();
    	skipTokens = false;
    	markerTokenIndex = 0;
    }
    
    public void reset(Monitor monitor, TokenStream tokStream)
    {
        this.monitor = monitor;
        this.tokStream = (TokenStream) tokStream;
        reset();
    }


    public void reset(TokenStream tokStream)
    {
        reset (null, tokStream);
    }


    public void reset(Monitor monitor, TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                                    NotBacktrackParseTableException
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
    	if (! prs.getBacktrack()) throw new NotBacktrackParseTableException();
    }
    
    public void reset(TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                   NotBacktrackParseTableException
    {
        reset(null, tokStream, prs, ra);
    }

    public BacktrackingParser() {}

    public BacktrackingParser(TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                           NotBacktrackParseTableException
    {
        reset(null, tokStream, prs, ra);
    }

    public BacktrackingParser(Monitor monitor, TokenStream tokStream, ParseTable prs, RuleAction ra) throws BadParseSymFileException,
                                                                                                            NotBacktrackParseTableException
    {
        reset(monitor, tokStream, prs, ra);
    }

    //
    // Allocate or reallocate all the stacks. Their sizes should always be the same.
    //
    public void reallocateOtherStacks(int start_token_index)
    {
        // assert(super.stateStack != null);
        if (this.actionStack == null)
        {
            this.actionStack = new int[super.stateStack.length];
            super.locationStack = new int[super.stateStack.length];
            super.parseStack = new Object[super.stateStack.length];

            actionStack[0] = 0;
            locationStack[0] = start_token_index;
        }
        else if (this.actionStack.length < super.stateStack.length)
        {
            int old_length = this.actionStack.length;

            System.arraycopy(this.actionStack, 0, this.actionStack = new int[super.stateStack.length], 0, old_length);
            System.arraycopy(super.locationStack, 0, super.locationStack = new int[super.stateStack.length], 0, old_length);
            System.arraycopy(super.parseStack, 0, super.parseStack = new Object[super.stateStack.length], 0, old_length);
        }
        return;
    }

    //
    // Always attempt to recover
    //
    public Object fuzzyParse() throws BadParseException
    {
        return fuzzyParseEntry(0, Integer.MAX_VALUE);
    }

    //
    // Recover up to max_error_count times and then quit
    //
    public Object fuzzyParse(int max_error_count) throws BadParseException
    {
        return fuzzyParseEntry(0, max_error_count);
    }

    //
    // Always attempt to recover
    //
    public Object fuzzyParseEntry(int marker_kind) throws BadParseException
    {
        return fuzzyParseEntry(marker_kind, Integer.MAX_VALUE);
    }

    //
    //
    //
    public Object fuzzyParseEntry(int marker_kind, int max_error_count) throws BadParseException
    {
        action.reset();
        tokStream.reset(); // Position at first token.
        reallocateStateStack();
        stateStackTop = 0;
        stateStack[0] = START_STATE;

        //
        // The tuple tokens will eventually contain the sequence 
        // of tokens that resulted in a successful parse. We leave
        // it up to the "Stream" implementer to define the predecessor
        // of the first token as he sees fit.
        //
        int first_token = tokStream.peek(),
            start_token = first_token,
            marker_token = getMarkerToken(marker_kind, first_token);
        tokens = new IntTuple(tokStream.getStreamLength());
        tokens.add(tokStream.getPrevious(first_token));

        int error_token = backtrackParse(action, marker_token);
        if (error_token != 0) // an error was detected?
        {
            if (! (tokStream instanceof IPrsStream))
                throw new TokenStreamNotIPrsStreamException();
            RecoveryParser rp = new RecoveryParser(this, monitor, action, tokens, (IPrsStream) tokStream, prs, max_error_count, 0);
            start_token = rp.recover(marker_token, error_token);
        }

        if (marker_token != 0 && start_token == first_token)
            tokens.add(marker_token);
        int t;
        for (t = start_token; tokStream.getKind(t) != EOFT_SYMBOL; t = tokStream.getNext(t))
            tokens.add(t);
        tokens.add(t);

        return parseActions(marker_kind);
    }

    //
    // Parse without attempting any Error token recovery
    //
    public Object parse() throws BadParseException
    {
        // without an argument parse() will ignore error productions
        return parseEntry(0, 0);
    }

    //
    // Parse input allowing up to max_error_count Error token recoveries.
    // When max_error_count is 0, no Error token recoveries occur.
    // When max_error is > 0, it limits the number of Error token recoveries.
    // When max_error is < 0, the number of error token recoveries is unlimited.
    // Also, such recoveries only require one token to be parsed beyond the recovery point.
    // (normally two tokens beyond the recovery point must be parsed)
    // Thus, a negative max_error_count should be used when error productions are used to 
    // skip tokens.
    //
    public Object parse(int max_error_count) throws BadParseException
    {
        return parseEntry(0, max_error_count);
    }

    //
    // Parse without attempting any Error token recovery
    //
    public Object parseEntry(int marker_kind) throws BadParseException
    {
        // without an argument parse() will ignore error productions
        return parseEntry(marker_kind, 0);
    }

    //
    // Parse input allowing up to max_error_count Error token recoveries.
    // When max_error_count is 0, no Error token recoveries occur.
    // When max_error is > 0, it limits the number of Error token recoveries.
    // When max_error is < 0, the number of error token recoveries is unlimited.
    // Also, such recoveries only require one token to be parsed beyond the recovery point.
    // (normally two tokens beyond the recovery point must be parsed)
    // Thus, a negative max_error_count should be used when error productions are used to 
    // skip tokens.
    //
    public Object parseEntry(int marker_kind, int max_error_count) throws BadParseException
    {
        action.reset();
        tokStream.reset(); // Position at first token.
        reallocateStateStack();
        stateStackTop = 0;
        stateStack[0] = START_STATE;

        skipTokens = max_error_count < 0;

        if (max_error_count > 0 && tokStream instanceof IPrsStream)
            max_error_count = 0;
        
        //
        // The tuple tokens will eventually contain the sequence 
        // of tokens that resulted in a successful parse. We leave
        // it up to the "Stream" implementer to define the predecessor
        // of the first token as he sees fit.
        //
        tokens = new IntTuple(tokStream.getStreamLength());
        tokens.add(tokStream.getPrevious(tokStream.peek()));

        int start_token_index = tokStream.peek(),
            repair_token = getMarkerToken(marker_kind, start_token_index),
            start_action_index = action.size(), // obviously 0
            temp_stack[] = new int[stateStackTop + 1];
        System.arraycopy(stateStack, 0, temp_stack, 0, temp_stack.length);
        
        int initial_error_token = backtrackParse(action, repair_token);
        for (int error_token = initial_error_token, count = 0;
             error_token != 0;
             error_token = backtrackParse(action, repair_token), count++)
        {
            if (count == max_error_count)
                throw new BadParseException(initial_error_token);
            action.reset(start_action_index);
            tokStream.reset(start_token_index);
            stateStackTop = temp_stack.length - 1;
            System.arraycopy(temp_stack, 0, stateStack, 0, temp_stack.length);
            reallocateOtherStacks(start_token_index);

            backtrackParseUpToError(repair_token, error_token);

            for (stateStackTop = findRecoveryStateIndex(stateStackTop);
                 stateStackTop >= 0;
                 stateStackTop = findRecoveryStateIndex(stateStackTop - 1))
            {
                int recovery_token = tokens.get(locationStack[stateStackTop] - 1);
                repair_token = errorRepair((IPrsStream) tokStream,
                                           (recovery_token >= start_token_index ? recovery_token : error_token),
                                           error_token);
                if (repair_token != 0)
                    break;
            }

            if (stateStackTop < 0)
                throw new BadParseException(initial_error_token);

            temp_stack = new int[stateStackTop + 1];
            System.arraycopy(stateStack, 0, temp_stack, 0, temp_stack.length);

            start_action_index = action.size();
            start_token_index = tokStream.peek();
        }

        if (repair_token != 0)
            tokens.add(repair_token);
        int t;
        for (t = start_token_index; tokStream.getKind(t) != EOFT_SYMBOL; t = tokStream.getNext(t))
            tokens.add(t);
        tokens.add(t);

        return parseActions(marker_kind);
    }

    //
    // Process reductions and continue...
    //
    private final void process_reductions()
    {
        do
        {
            stateStackTop -= (prs.rhs(currentAction) - 1);
            ra.ruleAction(currentAction);
            currentAction = prs.ntAction(stateStack[stateStackTop], prs.lhs(currentAction));
//if(currentAction <= NUM_RULES)
//System.err.println("Goto-reducing by rule " + currentAction);              
        } while(currentAction <= NUM_RULES);
//System.err.println("Goto state " + prs.originalState(currentAction));
        return;
    }

    //
    // Now do the final parse of the input based on the actions in
    // the list "action" and the sequence of tokens in list "tokens".
    //
    private Object parseActions(int marker_kind) throws BadParseException
    {
        int ti = -1,
            curtok;
        lastToken = tokens.get(++ti);
        curtok = tokens.get(++ti);
        allocateOtherStacks();

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
                return null;

            stateStack[++stateStackTop] = currentAction;
            locationStack[stateStackTop] = ti;

            currentAction = action.get(i);
            if (currentAction <= NUM_RULES)  // a reduce action?
            {
//System.err.println("reducing by rule " + currentAction);              
                stateStackTop--; // make reduction look like shift-reduction
                process_reductions();
            }
            else                   // a shift or shift-reduce action
            {
                if (tokStream.getKind(curtok) > NT_OFFSET) 
                {
                    ErrorToken badtok = (ErrorToken) ((PrsStream) tokStream).getIToken(curtok);
                    throw new BadParseException(badtok.getErrorToken().getTokenIndex()); // parseStack[stateStackTop] = ra.prostheticAst[prs.getProsthesisIndex(tokStream.getKind(curtok))].create(tokStream.getIToken(curtok));
                }
                lastToken = curtok;
                curtok = tokens.get(++ti);
                if (currentAction > ERROR_ACTION) // a shift-reduce action?
                {
                    currentAction -= ERROR_ACTION;
//System.err.println("Shift-reducing by rule " + currentAction + " on token " + tokStream.getName(lastToken));              
                    process_reductions();
                }
//else System.err.println("Shifting on token " + tokStream.getName(lastToken) + " to state " + prs.originalState(currentAction));              
            }
        }

        return parseStack[marker_kind == 0 ? 0 : 1];
    }

    //
    // Process reductions and continue...
    //
    private int process_backtrack_reductions(int act)
    {
        do
        {
            stateStackTop -= (prs.rhs(act) - 1);
            act = prs.ntAction(stateStack[stateStackTop], prs.lhs(act));
        } while(act <= NUM_RULES);

        return act;
    }

    //
    // This method is intended to be used by the type RecoveryParser.
    // Note that the action tuple passed here must be the same action
    // tuple that was passed down to RecoveryParser. It is passed back
    // to this method as documention.
    //
    int backtrackParse(int stack[], int stack_top, IntSegmentedTuple action, int initial_token)
    {
        stateStackTop = stack_top;
        System.arraycopy(stack, 0, stateStack, 0, stateStackTop + 1);
        // assert(this.action == action);
        return backtrackParse(action, initial_token);
    }

    //
    // Parse the input until either the parse completes successfully or
    // an error is encountered. This function returns an integer that
    // represents the last action that was executed by the parser. If
    // the parse was succesful, then the tuple "action" contains the
    // successful sequence of actions that was executed.
    //
    private int backtrackParse(IntSegmentedTuple action, int initial_token)
    {
        //
        // Allocate configuration stack.
        //
        ConfigurationStack configuration_stack = new ConfigurationStack(prs);

        //
        // Keep parsing until we successfully reach the end of file or
        // an error is encountered. The list of actions executed will
        // be stored in the "action" tuple.
        //
        int error_token = 0,
            maxStackTop = stateStackTop,
            start_token = tokStream.peek(),
            curtok = (initial_token > 0 ? initial_token : tokStream.getToken()),
            current_kind = tokStream.getKind(curtok),
            act = tAction(stateStack[stateStackTop], current_kind);

        //
        // The main driver loop
        //
        for (;;)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                return 0;

            if (act <= NUM_RULES)
            {
                action.add(act); // save this reduce action
                stateStackTop--;
                act = process_backtrack_reductions(act);
            }
            else if (act > ERROR_ACTION)
            {
                action.add(act);     // save this shift-reduce action
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                act = process_backtrack_reductions(act - ERROR_ACTION);
            }
            else if (act < ACCEPT_ACTION)
            {
                action.add(act);    // save this shift action
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
            }
            else if (act == ERROR_ACTION)
            {
                error_token = (error_token > curtok ? error_token : curtok);

                ConfigurationElement configuration = configuration_stack.pop();
                if (configuration == null)
                     act = ERROR_ACTION;
                else
                {
                    action.reset(configuration.action_length);
                    act = configuration.act;
                    curtok = configuration.curtok;
                    current_kind = tokStream.getKind(curtok);
                    tokStream.reset(curtok == initial_token
                                            ? start_token
                                            : tokStream.getNext(curtok));
                    stateStackTop = configuration.stack_top;
                    configuration.retrieveStack(stateStack);
                    continue;
                }
                break;
            }
            else if (act > ACCEPT_ACTION)
            {
                if (configuration_stack.findConfiguration(stateStack, stateStackTop, curtok))
                    act = ERROR_ACTION;
                else
                {
                    configuration_stack.push(stateStack, stateStackTop, act + 1, curtok, action.size());
                    act = prs.baseAction(act);
                    maxStackTop = stateStackTop > maxStackTop ? stateStackTop : maxStackTop;
                }
                continue;
            }
            else break; // assert(act == ACCEPT_ACTION);
            try
            {
                stateStack[++stateStackTop] = act;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStateStack();
                stateStack[stateStackTop] = act;
            }

            act = tAction(act, current_kind);
        }

        // System.out.println("****Max Number of configurations: " + configuration_stack.maxConfigurationSize());
        // for (ConfigurationElement configuration = configuration_stack.pop();
        //      configuration != null;
        //      configuration = configuration_stack.pop())
//        {
//System.out.println("    restart at position " + configuration.action_length +
//		           " on action " + configuration.act +
//		           " and token " + configuration.curtok +
//		           " with kind " + tokStream.getKind(configuration.curtok)
//		          );   	
//        }
//
        // System.out.println("****Number of elements in stack tree: " + configuration_stack.numStateElements());
        // System.out.println("****Number of elements in stacks: " + configuration_stack.stacksSize());
        // System.out.println("****Number of actions: " + action.size());
        // System.out.println("****Max Stack Size = " + maxStackTop);
        // System.out.flush();
        return (act == ERROR_ACTION ? error_token : 0);
    }

    private void backtrackParseUpToError(int initial_token, int error_token)
    {
        //
        // Allocate configuration stack.
        //
        ConfigurationStack configuration_stack = new ConfigurationStack(prs);

        //
        // Keep parsing until we successfully reach the end of file or
        // an error is encountered. The list of actions executed will
        // be stored in the "action" tuple.
        //
        int start_token = tokStream.peek(),
            curtok = (initial_token > 0 ? initial_token : tokStream.getToken()),
            current_kind = tokStream.getKind(curtok),
            act = tAction(stateStack[stateStackTop], current_kind);

        tokens.add(curtok);
        locationStack[stateStackTop] = tokens.size();
        actionStack[stateStackTop] = action.size();

        for (;;)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                return;

            if (act <= NUM_RULES)
            {
                action.add(act); // save this reduce action
                stateStackTop--;
                act = process_backtrack_reductions(act);
            }
            else if (act > ERROR_ACTION)
            {
                action.add(act);     // save this shift-reduce action
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                tokens.add(curtok);
                act = process_backtrack_reductions(act - ERROR_ACTION);
            }
            else if (act < ACCEPT_ACTION)
            {
                action.add(act);     // save this shift action
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                tokens.add(curtok);
            }
            else if (act == ERROR_ACTION)
            {
                if (curtok != error_token)
                {
                    ConfigurationElement configuration = configuration_stack.pop();
                    if (configuration == null)
                        act = ERROR_ACTION;
                    else
                    {
                        action.reset(configuration.action_length);
                        act = configuration.act;
                        int next_token_index = configuration.curtok;
                        tokens.reset(next_token_index);
                        curtok = tokens.get(next_token_index - 1);
                        current_kind = tokStream.getKind(curtok);
                        tokStream.reset(curtok == initial_token
                                                ? start_token
                                                : tokStream.getNext(curtok));
                        stateStackTop = configuration.stack_top;
                        configuration.retrieveStack(stateStack);
                        locationStack[stateStackTop] = tokens.size();
                        actionStack[stateStackTop] = action.size();
                        continue;
                    }
                }
                break;
            }
            else if (act > ACCEPT_ACTION)
            {
                if (configuration_stack.findConfiguration(stateStack, stateStackTop, tokens.size()))
                    act = ERROR_ACTION;
                else
                {
                    configuration_stack.push(stateStack, stateStackTop, act + 1, tokens.size(), action.size());
                    act = prs.baseAction(act);
                }
                continue;
            }
            else break; // assert(act == ACCEPT_ACTION);

            stateStack[++stateStackTop] = act; // no need to check if out of bounds
            locationStack[stateStackTop] = tokens.size();
            actionStack[stateStackTop] = action.size();
            act = tAction(act, current_kind);
        }

        // assert(curtok == error_token);

        return;
    }

    private boolean repairable(int error_token)
    {
        //
        // Allocate configuration stack.
        //
        ConfigurationStack configuration_stack = new ConfigurationStack(prs);

        //
        // Keep parsing until we successfully reach the end of file or
        // an error is encountered. The list of actions executed will
        // be stored in the "action" tuple.
        //
        int start_token = tokStream.peek(),
            final_token = tokStream.getStreamLength(), // unreachable
            curtok = 0,
            current_kind = ERROR_SYMBOL,
            act = tAction(stateStack[stateStackTop], current_kind);

        for (;;)
        {
            if (act <= NUM_RULES)
            {
                stateStackTop--;
                act = process_backtrack_reductions(act);
            }
            else if (act > ERROR_ACTION)
            {
                curtok = tokStream.getToken();
                if (curtok > final_token) return true;
                current_kind = tokStream.getKind(curtok);
                act = process_backtrack_reductions(act - ERROR_ACTION);
            }
            else if (act < ACCEPT_ACTION)
            {
                curtok = tokStream.getToken();
                if (curtok > final_token) return true;
                current_kind = tokStream.getKind(curtok);
            }
            else if (act == ERROR_ACTION)
            {
                ConfigurationElement configuration = configuration_stack.pop();
                if (configuration == null)
                     act = ERROR_ACTION;
                else
                {
                    stateStackTop = configuration.stack_top;
                    configuration.retrieveStack(stateStack);
                    act = configuration.act;
                    curtok = configuration.curtok;
                    if (curtok == 0)
                    {
                        current_kind = ERROR_SYMBOL;
                        tokStream.reset(start_token);
                    }
                    else
                    {
                        current_kind = tokStream.getKind(curtok);
                        tokStream.reset(tokStream.getNext(curtok));
                    }
                    continue;
                }
                break;
            }
            else if (act > ACCEPT_ACTION)
            {
                if (configuration_stack.findConfiguration(stateStack, stateStackTop, curtok))
                    act = ERROR_ACTION;
                else
                {
                    configuration_stack.push(stateStack, stateStackTop, act + 1, curtok, 0);
                    act = prs.baseAction(act);
                }
                continue;
            }
            else break; // assert(act == ACCEPT_ACTION);
            try
            {
                //
                // We consider a configuration to be acceptable for recovery
                // if we are able to consume enough symbols in the remainining
                // tokens to reach another potential recovery point past the
                // original error token.
                //
                if ((curtok > error_token) && (final_token == tokStream.getStreamLength()))
                {
                    //
                    // If the ERROR_SYMBOL is a valid Action Adjunct in the state
                    // "act" then we set the terminating token as the successor of
                    // the current token. I.e., we have to be able to parse at least
                    // two tokens past the resynch point before we claim victory.
                    //
                    if (recoverableState(act))
                        final_token = skipTokens ? curtok : tokStream.getNext(curtok);
                }

                stateStack[++stateStackTop] = act;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStateStack();
                stateStack[stateStackTop] = act;
            }

            act = tAction(act, current_kind);
        }

        //
        // If we can reach the end of the input successfully, we claim victory.
        //
        return (act == ACCEPT_ACTION);
    }

    private boolean recoverableState(int state)
    {
        for (int k = prs.asi(state); prs.asr(k) != 0; k++)
        {
           if (prs.asr(k) == ERROR_SYMBOL)
                return true;
        }
        return false;
    }

    private int findRecoveryStateIndex(int start_index)
    {
        int i;
        for (i = start_index; i >= 0; i--)
        {
            //
            // If the ERROR_SYMBOL is an Action Adjunct in state stateStack[i]
            // then chose i as the index of the state to recover on.
            //
            if (recoverableState(stateStack[i]))
                break;
        }

        if (i >= 0) // if a recoverable state, remove null reductions, if any.
        {
            int k;
            for (k = i - 1; k >= 0; k--)
            {
                if (locationStack[k] != locationStack[i])
                    break;
            }
            i = k + 1;
        }

        return i;
    }

    private int errorRepair(IPrsStream stream, int recovery_token, int error_token)
    {
        int temp_stack[] = new int[stateStackTop + 1];
        System.arraycopy(stateStack, 0, temp_stack, 0, temp_stack.length);
        for (;
             stream.getKind(recovery_token) != EOFT_SYMBOL;
             recovery_token = stream.getNext(recovery_token))
        {
            stream.reset(recovery_token);
            if (repairable(error_token))
                break;
            stateStackTop = temp_stack.length - 1;
            System.arraycopy(temp_stack, 0, stateStack, 0, temp_stack.length);
        }

        if (stream.getKind(recovery_token) == EOFT_SYMBOL)
        {
            stream.reset(recovery_token);
            if (! repairable(error_token))
            {
                stateStackTop = temp_stack.length - 1;
                System.arraycopy(temp_stack, 0, stateStack, 0, temp_stack.length);
                return 0;
            }
        }

        //
        //
        //
        stateStackTop = temp_stack.length - 1;
        System.arraycopy(temp_stack, 0, stateStack, 0, temp_stack.length);
        stream.reset(recovery_token);
        tokens.reset(locationStack[stateStackTop] - 1);
        action.reset(actionStack[stateStackTop]);

        return stream.makeErrorToken(tokens.get(locationStack[stateStackTop] - 1),
                                     stream.getPrevious(recovery_token),
                                     error_token,
                                     ERROR_SYMBOL);
    }

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
        return  (act > LA_STATE_OFFSET
                     ? lookahead(act, tokStream.peek())
                     : act);
    }
}
