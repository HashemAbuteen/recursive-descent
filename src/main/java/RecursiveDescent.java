import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecursiveDescent {
    private String token;
    private FileReader fileReader;
    private int line = 0;

    private int commentFlag =0;

    private char lastChar = 0;
    private boolean lastFlag = false;

    private boolean error = false;

    String[] reservedArray = {"program" , "begin" , "end" , "const" , "var" , "integer" , "real" ,
                                "char" , "mod" , "div" , "read" , "readln" , "write" , "writeln",
                                "if" , "then" , "else" , "while" , "do" , "repeat" , "until"};

    List<String> reservedList;

    public RecursiveDescent(File file) throws FileNotFoundException {
        fileReader = new FileReader(file);
        reservedList = new ArrayList<>(Arrays.asList(reservedArray));
    }

    private void getToken() {
        char c;
        if(lastFlag){
            c = lastChar;
            lastFlag = false;
        }
        else {
            c = 0;
            try {
                c = (char) fileReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(c == -1){
            token = "";
            reportError("End of file ");
        }
        else if(c == '\n'){
            line ++;
            getToken();
        } else if (c == ' ') {
            getToken();
        }else if (c == '\r') {
            getToken();
        } else if (Character.isDigit(c)) {
            token = c + "";
            try {
                c = (char) fileReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (Character.isDigit(c) || c == '.') {
                token += c;
                try {
                    c = (char) fileReader.read();
                    lastChar = c;
                    lastFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (Character.isAlphabetic(c) || c == '_') {
            token = c + "";
            try {
                c = (char) fileReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (Character.isAlphabetic(c) || c == '_' || Character.isDigit(c)) {
                token += c;
                try {
                    c = (char) fileReader.read();
                    lastChar = c;
                    lastFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            token = c + "";
        }
    }


    private void reportError(String s) {
        System.out.println("Error : " + s + " at line : " + line);
        error = true;
    }

    private void reportError(){
        reportError("");
    }

    private void success() {
        if(error) return;
        System.out.println("Compiled Succefuly, No syntax Errors");
    }

    private boolean isName(String token) {
        if(reservedList.contains(token)){
            return false;
        }
        for(int i = 0 ; i < token.length() ; i++){
            if( i == 0 &&  Character.isDigit(token.charAt(i))){
                return false;
            }
            if(token.charAt(i) == '_' || Character.isAlphabetic(token.charAt(i)) || Character.isDigit(token.charAt(i))){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private boolean isFloat(String token){
        boolean pointFlag = false;
        for(int i = 0 ; i < token.length() ; i++){
            if(token.charAt(i) =='.'){
                if(pointFlag){
                    return false;
                } else {
                    pointFlag = true;
                }
            } else if(Character.isDigit(token.charAt(i))){
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean isInteger(String token){
        for(int i = 0 ; i < token.length() ; i++){
            if(Character.isDigit(token.charAt(i))){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public void compile() {
        getToken();
        heading();
        declarations();
        block();
        if(token.equals(".")){
            success();
        }
        else{
            reportError("messing \".\"");
        }
    }

    private void heading(){
        if(token.equals("program")){
            getToken();
            if(isName(token)){
                getToken();
                if(token.equals(";")){
                    getToken();
                }
                else {
                    reportError("messing ;");
                }
            }
            else {
                reportError("Error in program-name");
            }
        }
    }

    private void block(){
        if(token.equals("begin")){
            getToken();
            stmtList();
            if(token.equals("end")){
                getToken();
            }
            else {
                reportError("messing end");
            }
        }
        else {
            reportError("messing begin");
        }
    }

    private void declarations(){
        constDecl();
        varDecl();
    }

    private void constDecl(){
        if(token.equals("const")){
            getToken();
            constList();
        }
        else {
            //I do not check for if token equals follow because the function that called this does
            return;
        }
    }

    private void constList(){
        if(isName(token)){
            getToken();
            if(token.equals("=")){
                getToken();
                value();
                if(token.equals(";")){
                    getToken();
                    constList();
                }else reportError("messing ;");
            }
            else reportError("messing =");
        }
        else {
            return;
        }
    }

    private void varDecl(){
        if(token.equals("var")){
            getToken();
            varList();
        }
        else {
            return;
        }
    }

    private void varList(){
        if(isName(token)){
            varItem();
            if(token.equals(";")){
                getToken();
                varList();
            } else reportError("messing ;");
        }
        else {
            return;
        }
    }

    private void varItem(){
        nameList();
        if(token.equals(":")){
            getToken();
            dataType();
        } else reportError("messing :");
    }

    private void nameList(){
        if(isName(token)){
            getToken();
            moreNames();
        } else reportError();
    }

    private void moreNames(){
        if(token.equals(",")){
            getToken();
            nameList();
        } else return;
    }

    private void dataType(){
        if(token.equals("integer")){
            getToken();
        } else if(token.equals("real")){
            getToken();
        } else if(token.equals("char")){
            getToken();
        } else reportError(token + " is not a data type");
    }

    private void stmtList(){
        if(token.equals("read")||token.equals("readln") ||token.equals("write")||token.equals("writeln")||
                token.equals("if")||token.equals("while")||token.equals("repeat")||token.equals("begin")){
            statement();
            if (token.equals(";")) {
                getToken();
                stmtList();
            } else reportError("messing ;");
        }else {
            return;
        }
    }

    private void statement(){
        if(isName(token)){
            assStmt();
        } else if(token.equals("read")||token.equals("readln")){
            readStmt();
        } else if(token.equals("write")||token.equals("writeln")){
            writeStmt();
        } else if(token.equals("if")){
            ifStmt();
        } else if(token.equals("while")){
            whileStmt();
        } else if(token.equals("repeat")){
            repeatStmt();
        } else if(token.equals("begin")){
            block();
        } else reportError();
    }

    private void assStmt(){
        if(isName(token)){
            getToken();
            if(token.equals(":")){
                getToken();
            } else if(token.equals("=")){
                getToken();
                exp();
            } else reportError("messing = after :");
        } else reportError();
    }

    private void exp(){
        term();
        expPrime();
    }

    private void expPrime(){
        if(token.equals("-")||token.equals("+")) {
            addOper();
            term();
            expPrime();
        }
        else {
            return;
        }
    }

    private void term(){
        factor();
        termPrime();
    }

    private void termPrime(){
        if(token.equals("*")||token.equals("/")||token.equals("mod")||token.equals("div")){
            mulOper();
            factor();
            termPrime();
        } else return;
    }

    private void factor(){
        if(token.equals("(")){
            getToken();
            exp();
            if(token.equals(")")){
                getToken();
            } else reportError("messing )");
        } else if(isName(token)){
            nameValue();
        } else reportError();
    }

    private void addOper(){
        if(token.equals("+")){
            getToken();
        } else if(token.equals("-")){
            getToken();
        } else reportError();
    }

    private void mulOper(){
        if(token.equals("*")){
            getToken();
        } else if(token.equals("/")){
            getToken();
        } else if(token.equals("mod")){
            getToken();
        } else if(token.equals("div")){
            getToken();
        } else reportError();
    }

    private void value(){
        if(isInteger(token)){
            getToken();
        } else if(isFloat(token)){
            getToken();
        } else reportError("wrong number format ");
    }

    private void readStmt() {
        if (token.equals("read")) {
            getToken();
            if (token.equals("(")) {
                getToken();
                nameList();
                if (token.equals(")")) {
                    getToken();
                } else reportError("Messing )");
            } else reportError("Messing (");
        } else if (token.equals("readln")) {
            getToken();
            if (token.equals("(")) {
                getToken();
                nameList();
                if (token.equals(")")) {
                    getToken();
                } else reportError("Messing )");
            } else reportError("Messing (");
        } else reportError();
    }

    private void writeStmt() {
        if (token.equals("write")) {
            getToken();
            if (token.equals("(")) {
                getToken();
                nameList();
                if (token.equals(")")) {
                    getToken();
                } else reportError("Messing )");
            } else reportError("Messing (");
        } else if (token.equals("writeln")) {
            getToken();
            if (token.equals("(")) {
                getToken();
                nameList();
                if (token.equals(")")) {
                    getToken();
                } else reportError("Messing )");
            } else reportError("Messing (");
        } else reportError();
    }

    private void ifStmt(){
        if (token.equals("if")) {
            getToken();
            condition();
            if (token.equals("then")) {
                getToken();
                statement();
                elsePart();
            } else reportError("Messing then");
        } else reportError();
    }

    private void elsePart(){
        if (token.equals("else")) {
            getToken();
            statement();
        } else{
            return;
        }
    }

    private void whileStmt(){
        if (token.equals("while")) {
            getToken();
            condition();
            if (token.equals("do")) {
                getToken();
                statement();
            } else reportError("Messing do");
        } else reportError();
    }

    private void repeatStmt(){
        if (token.equals("repeat")) {
            getToken();
            stmtList();
            if (token.equals("until")) {
                getToken();
                condition();
            } else reportError("Messing until");
        } else reportError();
    }

    private void condition(){
        nameValue();
        releationalOper();
        nameValue();
    }

    private void nameValue(){
        if(isName(token)){
            getToken();
        } else if(isInteger(token) || isFloat(token)){
            value();
        }
        else reportError();
    }

    private void releationalOper(){
        if (token.equals("=")) {
            getToken();
        } else if (token.equals("<")) {
            getToken();
            if (token.equals(">")) {
                getToken();
            }else if (token.equals("=")){
                getToken();
            }
        } else if (token.equals(">")) {
            getToken();
            if (token.equals("=")){
                getToken();
            }
        } else reportError("messing a relational operator");
    }


}
