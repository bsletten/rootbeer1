// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 OpenCL.g 2010-10-24 13:57:48

package edu.syr.pcpratts.compressor;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class OpenCLLexer extends Lexer {
    public static final int INT_LITERAL=9;
    public static final int VARIABLE=12;
    public static final int SPECIAL=4;
    public static final int SEP=5;
    public static final int POINT=8;
    public static final int AMP=7;
    public static final int WHITESPACE=10;
    public static final int STRING_DELIM=11;
    public static final int EOF=-1;
    public static final int END_OF_STATEMENT=6;

    // delegates
    // delegators

    public OpenCLLexer() {;} 
    public OpenCLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public OpenCLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "OpenCL.g"; }

    // $ANTLR start "SPECIAL"
    public final void mSPECIAL() throws RecognitionException {
        try {
            int _type = SPECIAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:36:8: ( ( '&&' | '||' | '==' | '=' | '>' | '>=' | '<=' | '<' | '/' | '?' | '+' | '-' | ':' | '//' | '/*' | '\\\\*' | '+=' | '-=' | '++' | '%' | '--' | '>>' | '<<' | '\\\\' | '!=' | '\\\\\"' ) )
            // OpenCL.g:36:10: ( '&&' | '||' | '==' | '=' | '>' | '>=' | '<=' | '<' | '/' | '?' | '+' | '-' | ':' | '//' | '/*' | '\\\\*' | '+=' | '-=' | '++' | '%' | '--' | '>>' | '<<' | '\\\\' | '!=' | '\\\\\"' )
            {
            // OpenCL.g:36:10: ( '&&' | '||' | '==' | '=' | '>' | '>=' | '<=' | '<' | '/' | '?' | '+' | '-' | ':' | '//' | '/*' | '\\\\*' | '+=' | '-=' | '++' | '%' | '--' | '>>' | '<<' | '\\\\' | '!=' | '\\\\\"' )
            int alt1=26;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // OpenCL.g:36:11: '&&'
                    {
                    match("&&"); 


                    }
                    break;
                case 2 :
                    // OpenCL.g:36:18: '||'
                    {
                    match("||"); 


                    }
                    break;
                case 3 :
                    // OpenCL.g:36:25: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 4 :
                    // OpenCL.g:36:32: '='
                    {
                    match('='); 

                    }
                    break;
                case 5 :
                    // OpenCL.g:36:38: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 6 :
                    // OpenCL.g:36:44: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 7 :
                    // OpenCL.g:36:51: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 8 :
                    // OpenCL.g:36:58: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 9 :
                    // OpenCL.g:36:64: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 10 :
                    // OpenCL.g:36:70: '?'
                    {
                    match('?'); 

                    }
                    break;
                case 11 :
                    // OpenCL.g:36:76: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 12 :
                    // OpenCL.g:36:82: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 13 :
                    // OpenCL.g:36:88: ':'
                    {
                    match(':'); 

                    }
                    break;
                case 14 :
                    // OpenCL.g:36:94: '//'
                    {
                    match("//"); 


                    }
                    break;
                case 15 :
                    // OpenCL.g:36:101: '/*'
                    {
                    match("/*"); 


                    }
                    break;
                case 16 :
                    // OpenCL.g:36:108: '\\\\*'
                    {
                    match("\\*"); 


                    }
                    break;
                case 17 :
                    // OpenCL.g:36:116: '+='
                    {
                    match("+="); 


                    }
                    break;
                case 18 :
                    // OpenCL.g:36:123: '-='
                    {
                    match("-="); 


                    }
                    break;
                case 19 :
                    // OpenCL.g:36:130: '++'
                    {
                    match("++"); 


                    }
                    break;
                case 20 :
                    // OpenCL.g:37:12: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 21 :
                    // OpenCL.g:37:18: '--'
                    {
                    match("--"); 


                    }
                    break;
                case 22 :
                    // OpenCL.g:37:25: '>>'
                    {
                    match(">>"); 


                    }
                    break;
                case 23 :
                    // OpenCL.g:37:32: '<<'
                    {
                    match("<<"); 


                    }
                    break;
                case 24 :
                    // OpenCL.g:37:39: '\\\\'
                    {
                    match('\\'); 

                    }
                    break;
                case 25 :
                    // OpenCL.g:37:46: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 26 :
                    // OpenCL.g:37:53: '\\\\\"'
                    {
                    match("\\\""); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SPECIAL"

    // $ANTLR start "SEP"
    public final void mSEP() throws RecognitionException {
        try {
            int _type = SEP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:38:4: ( ( '{' | '}' | ')' | '(' | ',' | '[' | ']' | '!' | '@' | '.' ) )
            // OpenCL.g:38:6: ( '{' | '}' | ')' | '(' | ',' | '[' | ']' | '!' | '@' | '.' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='(' && input.LA(1)<=')')||input.LA(1)==','||input.LA(1)=='.'||input.LA(1)=='@'||input.LA(1)=='['||input.LA(1)==']'||input.LA(1)=='{'||input.LA(1)=='}' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEP"

    // $ANTLR start "END_OF_STATEMENT"
    public final void mEND_OF_STATEMENT() throws RecognitionException {
        try {
            int _type = END_OF_STATEMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:39:17: ( ';' )
            // OpenCL.g:39:19: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "END_OF_STATEMENT"

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:40:4: ( '&' )
            // OpenCL.g:40:6: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMP"

    // $ANTLR start "POINT"
    public final void mPOINT() throws RecognitionException {
        try {
            int _type = POINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:41:6: ( '*' )
            // OpenCL.g:41:8: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POINT"

    // $ANTLR start "STRING_DELIM"
    public final void mSTRING_DELIM() throws RecognitionException {
        try {
            int _type = STRING_DELIM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:42:13: ( ( '\"' | '\\'' ) )
            // OpenCL.g:42:15: ( '\"' | '\\'' )
            {
            if ( input.LA(1)=='\"'||input.LA(1)=='\'' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_DELIM"

    // $ANTLR start "VARIABLE"
    public final void mVARIABLE() throws RecognitionException {
        try {
            int _type = VARIABLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:43:9: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '#' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '#' | '0' .. '9' )* )
            // OpenCL.g:43:11: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '#' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '#' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='#' && input.LA(1)<='$')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // OpenCL.g:43:51: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '#' | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='#' && LA2_0<='$')||(LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // OpenCL.g:
            	    {
            	    if ( (input.LA(1)>='#' && input.LA(1)<='$')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VARIABLE"

    // $ANTLR start "INT_LITERAL"
    public final void mINT_LITERAL() throws RecognitionException {
        try {
            int _type = INT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:44:12: ( ( ( '-' )? ( '0' .. '9' | '.' )+ ( 'L' )? ) | ( '0x' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ ( 'L' )? ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0>='-' && LA8_0<='.')||(LA8_0>='1' && LA8_0<='9')) ) {
                alt8=1;
            }
            else if ( (LA8_0=='0') ) {
                int LA8_2 = input.LA(2);

                if ( (LA8_2=='x') ) {
                    alt8=2;
                }
                else {
                    alt8=1;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // OpenCL.g:44:14: ( ( '-' )? ( '0' .. '9' | '.' )+ ( 'L' )? )
                    {
                    // OpenCL.g:44:14: ( ( '-' )? ( '0' .. '9' | '.' )+ ( 'L' )? )
                    // OpenCL.g:44:15: ( '-' )? ( '0' .. '9' | '.' )+ ( 'L' )?
                    {
                    // OpenCL.g:44:15: ( '-' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='-') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // OpenCL.g:44:15: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }

                    // OpenCL.g:44:20: ( '0' .. '9' | '.' )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='.'||(LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // OpenCL.g:
                    	    {
                    	    if ( input.LA(1)=='.'||(input.LA(1)>='0' && input.LA(1)<='9') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);

                    // OpenCL.g:44:39: ( 'L' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='L') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // OpenCL.g:44:39: 'L'
                            {
                            match('L'); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // OpenCL.g:44:47: ( '0x' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ ( 'L' )? )
                    {
                    // OpenCL.g:44:47: ( '0x' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ ( 'L' )? )
                    // OpenCL.g:44:48: '0x' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ ( 'L' )?
                    {
                    match("0x"); 

                    // OpenCL.g:44:53: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')||(LA6_0>='A' && LA6_0<='F')||(LA6_0>='a' && LA6_0<='f')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // OpenCL.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    // OpenCL.g:44:87: ( 'L' )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='L') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // OpenCL.g:44:87: 'L'
                            {
                            match('L'); 

                            }
                            break;

                    }


                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT_LITERAL"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // OpenCL.g:45:12: ( ( ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' | '\\uffff' )+ )
            // OpenCL.g:45:14: ( ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' | '\\uffff' )+
            {
            // OpenCL.g:45:14: ( ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' | '\\uffff' )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>='\t' && LA9_0<='\n')||(LA9_0>='\f' && LA9_0<='\r')||LA9_0==' '||LA9_0=='\uFFFF') ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // OpenCL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' '||input.LA(1)=='\uFFFF' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    public void mTokens() throws RecognitionException {
        // OpenCL.g:1:8: ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | STRING_DELIM | VARIABLE | INT_LITERAL | WHITESPACE )
        int alt10=9;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // OpenCL.g:1:10: SPECIAL
                {
                mSPECIAL(); 

                }
                break;
            case 2 :
                // OpenCL.g:1:18: SEP
                {
                mSEP(); 

                }
                break;
            case 3 :
                // OpenCL.g:1:22: END_OF_STATEMENT
                {
                mEND_OF_STATEMENT(); 

                }
                break;
            case 4 :
                // OpenCL.g:1:39: AMP
                {
                mAMP(); 

                }
                break;
            case 5 :
                // OpenCL.g:1:43: POINT
                {
                mPOINT(); 

                }
                break;
            case 6 :
                // OpenCL.g:1:49: STRING_DELIM
                {
                mSTRING_DELIM(); 

                }
                break;
            case 7 :
                // OpenCL.g:1:62: VARIABLE
                {
                mVARIABLE(); 

                }
                break;
            case 8 :
                // OpenCL.g:1:71: INT_LITERAL
                {
                mINT_LITERAL(); 

                }
                break;
            case 9 :
                // OpenCL.g:1:83: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;

        }

    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA1_eotS =
        "\3\uffff\1\17\1\22\1\25\1\30\1\uffff\1\33\1\36\1\uffff\1\41\26\uffff";
    static final String DFA1_eofS =
        "\42\uffff";
    static final String DFA1_minS =
        "\1\41\2\uffff\2\75\1\74\1\52\1\uffff\1\53\1\55\1\uffff\1\42\26\uffff";
    static final String DFA1_maxS =
        "\1\174\2\uffff\1\75\1\76\1\75\1\57\1\uffff\2\75\1\uffff\1\52\26"+
        "\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\1\2\4\uffff\1\12\2\uffff\1\15\1\uffff\1\24\1\31\1\3"+
        "\1\4\1\6\1\26\1\5\1\7\1\27\1\10\1\16\1\17\1\11\1\21\1\23\1\13\1"+
        "\22\1\25\1\14\1\20\1\32\1\30";
    static final String DFA1_specialS =
        "\42\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\15\3\uffff\1\14\1\1\4\uffff\1\10\1\uffff\1\11\1\uffff\1\6"+
            "\12\uffff\1\12\1\uffff\1\5\1\3\1\4\1\7\34\uffff\1\13\37\uffff"+
            "\1\2",
            "",
            "",
            "\1\16",
            "\1\20\1\21",
            "\1\24\1\23",
            "\1\27\4\uffff\1\26",
            "",
            "\1\32\21\uffff\1\31",
            "\1\35\17\uffff\1\34",
            "",
            "\1\40\7\uffff\1\37",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "36:10: ( '&&' | '||' | '==' | '=' | '>' | '>=' | '<=' | '<' | '/' | '?' | '+' | '-' | ':' | '//' | '/*' | '\\\\*' | '+=' | '-=' | '++' | '%' | '--' | '>>' | '<<' | '\\\\' | '!=' | '\\\\\"' )";
        }
    }
    static final String DFA10_eotS =
        "\1\uffff\1\15\1\uffff\1\2\2\12\10\uffff";
    static final String DFA10_eofS =
        "\16\uffff";
    static final String DFA10_minS =
        "\1\11\1\46\1\uffff\1\56\1\75\1\56\10\uffff";
    static final String DFA10_maxS =
        "\1\uffff\1\46\1\uffff\1\71\1\75\1\114\10\uffff";
    static final String DFA10_acceptS =
        "\2\uffff\1\1\3\uffff\1\3\1\5\1\6\1\7\1\2\1\10\1\11\1\4";
    static final String DFA10_specialS =
        "\16\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\14\1\uffff\2\14\22\uffff\1\14\1\4\1\10\2\11\1\2\1\1\1\10"+
            "\2\12\1\7\1\2\1\12\1\3\1\5\1\2\12\13\1\2\1\6\4\2\1\12\32\11"+
            "\1\12\1\2\1\12\1\uffff\1\11\1\uffff\32\11\1\12\1\2\1\12\uff81"+
            "\uffff\1\14",
            "\1\2",
            "",
            "\1\13\1\uffff\12\13",
            "\1\2",
            "\1\13\1\uffff\12\13\22\uffff\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | STRING_DELIM | VARIABLE | INT_LITERAL | WHITESPACE );";
        }
    }
 

}