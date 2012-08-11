// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 OpenCL.g 2010-10-24 13:57:48

package edu.syr.pcpratts.compressor;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class OpenCLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SPECIAL", "SEP", "END_OF_STATEMENT", "AMP", "POINT", "INT_LITERAL", "WHITESPACE", "STRING_DELIM", "VARIABLE"
    };
    public static final int INT_LITERAL=9;
    public static final int VARIABLE=12;
    public static final int SPECIAL=4;
    public static final int SEP=5;
    public static final int POINT=8;
    public static final int AMP=7;
    public static final int STRING_DELIM=11;
    public static final int WHITESPACE=10;
    public static final int EOF=-1;
    public static final int END_OF_STATEMENT=6;

    // delegates
    // delegators


        public OpenCLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public OpenCLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return OpenCLParser.tokenNames; }
    public String getGrammarFileName() { return "OpenCL.g"; }


    public static class program_return extends ParserRuleReturnScope {
        public List<String> ret;
        public List<Boolean> modify;
        public List<Boolean> string;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "program"
    // OpenCL.g:23:1: program returns [List<String> ret, List<Boolean> modify, List<Boolean> string] : ( (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) ) | e3= STRING_DELIM | e2= VARIABLE )* EOF ;
    public final OpenCLParser.program_return program() throws RecognitionException {
        OpenCLParser.program_return retval = new OpenCLParser.program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token e1=null;
        Token e3=null;
        Token e2=null;
        Token EOF1=null;

        Object e1_tree=null;
        Object e3_tree=null;
        Object e2_tree=null;
        Object EOF1_tree=null;


          retval.ret = new ArrayList<String>();
          retval.modify = new ArrayList<Boolean>();
          retval.string = new ArrayList<Boolean>();

        try {
            // OpenCL.g:29:3: ( ( (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) ) | e3= STRING_DELIM | e2= VARIABLE )* EOF )
            // OpenCL.g:29:5: ( (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) ) | e3= STRING_DELIM | e2= VARIABLE )* EOF
            {
            root_0 = (Object)adaptor.nil();

            // OpenCL.g:29:5: ( (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) ) | e3= STRING_DELIM | e2= VARIABLE )*
            loop1:
            do {
                int alt1=4;
                switch ( input.LA(1) ) {
                case SPECIAL:
                case SEP:
                case END_OF_STATEMENT:
                case AMP:
                case POINT:
                case INT_LITERAL:
                case WHITESPACE:
                    {
                    alt1=1;
                    }
                    break;
                case STRING_DELIM:
                    {
                    alt1=2;
                    }
                    break;
                case VARIABLE:
                    {
                    alt1=3;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // OpenCL.g:29:7: (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) )
            	    {
            	    // OpenCL.g:29:7: (e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE ) )
            	    // OpenCL.g:29:8: e1= ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE )
            	    {
            	    e1=(Token)input.LT(1);
            	    if ( (input.LA(1)>=SPECIAL && input.LA(1)<=WHITESPACE) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, (Object)adaptor.create(e1));
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	     retval.ret.add((e1!=null?e1.getText():null)); retval.modify.add(false); retval.string.add(false); 

            	    }


            	    }
            	    break;
            	case 2 :
            	    // OpenCL.g:30:7: e3= STRING_DELIM
            	    {
            	    e3=(Token)match(input,STRING_DELIM,FOLLOW_STRING_DELIM_in_program114); 
            	    e3_tree = (Object)adaptor.create(e3);
            	    adaptor.addChild(root_0, e3_tree);

            	     retval.ret.add((e3!=null?e3.getText():null)); retval.modify.add(false); retval.string.add(true); 

            	    }
            	    break;
            	case 3 :
            	    // OpenCL.g:31:7: e2= VARIABLE
            	    {
            	    e2=(Token)match(input,VARIABLE,FOLLOW_VARIABLE_in_program126); 
            	    e2_tree = (Object)adaptor.create(e2);
            	    adaptor.addChild(root_0, e2_tree);

            	     retval.ret.add((e2!=null?e2.getText():null)); retval.modify.add(true); retval.string.add(false); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            EOF1=(Token)match(input,EOF,FOLLOW_EOF_in_program133); 
            EOF1_tree = (Object)adaptor.create(EOF1);
            adaptor.addChild(root_0, EOF1_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

            catch (RecognitionException e) {
                throw e;
            }
        finally {
        }
        return retval;
    }
    // $ANTLR end "program"

    // Delegated rules


 

    public static final BitSet FOLLOW_set_in_program73 = new BitSet(new long[]{0x0000000000001FF0L});
    public static final BitSet FOLLOW_STRING_DELIM_in_program114 = new BitSet(new long[]{0x0000000000001FF0L});
    public static final BitSet FOLLOW_VARIABLE_in_program126 = new BitSet(new long[]{0x0000000000001FF0L});
    public static final BitSet FOLLOW_EOF_in_program133 = new BitSet(new long[]{0x0000000000000002L});

}