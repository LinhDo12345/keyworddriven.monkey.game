package execute;

import common.keywords.KeyWords;
import common.utility.Constanst;
import common.utility.ExcelUtils;
import common.utility.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RunTestScript {
    public RunTestScript() {
        keyWord = new KeyWords();
        method = keyWord.getClass().getMethods();
    }
    public static void main(String[] args) throws IOException {
        Log.resetFileLog();
        scopePath = Constanst.PROJECT_PATH +Constanst.SCOPE_FILE_PATH;
        ExcelUtils.setExcelFile(scopePath);
        RunTestScript runTestScript = new RunTestScript();
        runTestScript.execute();
    }

    private void execute() {
        int iTotalFeature = ExcelUtils.getRowCount(Constanst.SCOPE_SHEET);
        for (int i = 0; i<iTotalFeature;i++){
            sRunMode = ExcelUtils.getCellData(i,Constanst.RUN_MODE,Constanst.SCOPE_SHEET);
            if(sRunMode.equals(Constanst.YES)) {
                tcPath = Constanst.PROJECT_PATH +Constanst.TESTCASE_FILE_PATH + ExcelUtils.getCellData(i,Constanst.TESTCASE_FILE_NAME,Constanst.SCOPE_SHEET)+".xlsx";
                ExcelUtils.setExcelFile(tcPath);
                execute_testcases();
                ExcelUtils.setExcelFile(scopePath);
                ExcelUtils.setCellData(tcResult, i, Constanst.STATUS_GAME, Constanst.SCOPE_SHEET, scopePath);
            }
        }
    }

    //region TESTCASE
    private void execute_testcases() {
        int iTotalTestCase = ExcelUtils.getRowCount(Constanst.TESTCASE_SHEET);
        for(int i =0; i<iTotalTestCase;i++) {
            sTestCaseID = ExcelUtils.getCellData(i, Constanst.TESTCASE_ID, Constanst.TESTCASE_SHEET);
            Log.info("TC: " + sTestCaseID);
            if (!sTestCaseID.equals(Constanst.TC_LABEL)) {
                rangeStepByTestCase();
                if (result != Constanst.SKIP) {
                    tcResult = Constanst.PASS;
                    execute_steps();
                    onResultTestcase(tcResult, "", i);
                } else
                    onResultTestcase(Constanst.SKIP, error, i);
            }
        }
    }

    private Object[] getParam(String params, String data){
        ArrayList<Object> objs = new ArrayList<>();
        if (!params.equals("")) {
            if (params.contains(",")) {
                for (String value : params.split(",")) {
                    objs.add(value);
                }
            } else {
                objs.add(params);
            }
            if(!data.equals("")){
                objs.add(data);
            }
            return objs.toArray();
        } else {
            return null;
        }
    }

    private void onResultTestcase(String status, String message, int rowNumber) {
        ExcelUtils.setCellData(status, rowNumber, Constanst.TESTCASE_STATUS, Constanst.TESTCASE_SHEET, tcPath);
        ExcelUtils.setCellData(message,  rowNumber, Constanst.TESTCASE_ERROR, Constanst.TESTCASE_SHEET, tcPath);
    }
    //endregion TESTCASE

    //region TEST STEP

    private void rangeStepByTestCase(){
        iTestStep = ExcelUtils.getRowContains(sTestCaseID,Constanst.TESTCASE_ID,Constanst.TEST_STEP_SHEET);
        lastTestStep = ExcelUtils.getTestStepCount(Constanst.TEST_STEP_SHEET,sTestCaseID,iTestStep);
    }

    private void execute_steps(){
        result = Constanst.PASS;
        for (; iTestStep < lastTestStep; iTestStep++) {
            process = ExcelUtils.getCellData(iTestStep, Constanst.PROCEED, Constanst.TEST_STEP_SHEET);
            if(process.equals(Constanst.PROCESS_YES)) {

                sActionKeyword = ExcelUtils.getCellData(iTestStep, Constanst.KEYWORD, Constanst.TEST_STEP_SHEET);
                params = ExcelUtils.getCellData(iTestStep, Constanst.PARAMS, Constanst.TEST_STEP_SHEET);
                dataSet = ExcelUtils.getCellData(iTestStep, Constanst.DATA_SET, Constanst.TEST_STEP_SHEET);
                description = ExcelUtils.getCellData(iTestStep, Constanst.DESCRIPTION, Constanst.TEST_STEP_SHEET);
                testStep = ExcelUtils.getCellData(iTestStep, Constanst.TEST_STEP, Constanst.TEST_STEP_SHEET);

                if (result != Constanst.SKIP) {
                    execute_action(iTestStep, dataSet);
                    verifyStep(iTestStep);
                }

                onResultStep(result, error, iTestStep);
                error = "";

                if (result == Constanst.FAIL)
                    tcResult = Constanst.FAIL;
            }
        }
    }

    private void execute_action(int numberStep,String data){
        try {
            result = Constanst.PASS;
            param = getParam(params,data);
            paramCount = (param == null) ? 0: param.length;

            for (int i = 0; i < method.length; i++) {

                if (method[i].getName().equals(sActionKeyword) && method[i].getParameterCount() == paramCount) {
                    Log.info(testStep +":  "+description);
                    if (paramCount > 0) {
                        String type = String.valueOf(method[i].getReturnType());
                        if (!type.equals("void")) {
                            RunTestScript.actual = (String) method[i].invoke(keyWord, param);
                            Log.info(description);
                            keyWord.check(actual, expected);
                        } else {
                            method[i].invoke(keyWord, param);
                        }
                    } else {
                        method[i].invoke(keyWord, null);
                    }
                    break;
                }
            }
        }catch (Throwable e) {
            Log.error("Method execute_action | Exception desc : " + e.getMessage());
            onResultStep(Constanst.FAIL,error,numberStep);
        }
    }

    // region verify result after each step
    private void verifyStep(int numberStep){
        try{
            sActionKeyword = ExcelUtils.getCellData(numberStep, Constanst.VERIFY_STEP, Constanst.TEST_STEP_SHEET);
            params = ExcelUtils.getCellData(numberStep, Constanst.PARAM_VERIFY_STEP, Constanst.TEST_STEP_SHEET);

            if(!sActionKeyword.equals("")){
                if(result == Constanst.PASS) {
                    expected = ExcelUtils.getCellData(numberStep,Constanst.EXPECTED,Constanst.TEST_STEP_SHEET);
                    description = "Check - " +description;
                    execute_action(numberStep, "");
                }
            }
        }catch (Throwable e){
            error = "Method verify | Exception desc : " + e.getMessage();
            Log.error(error);
            onFail( error);
            /*onResultStep(result,error,numberStep);*/
        }
        onResultStep(result,error,numberStep);
    }
    // endregion verify result after each step

    //region RESULT
    private void onResultStep(String status, String message, int rowNumber ){
        ExcelUtils.setCellData(status, rowNumber, Constanst.RESULT, Constanst.TEST_STEP_SHEET, tcPath);
        ExcelUtils.setCellData(message,  rowNumber, Constanst.ERROR, Constanst.TEST_STEP_SHEET, tcPath);
    }
    public static void onFail(String message) {
        Log.warn(message);
        result = Constanst.FAIL;
        error = message;
    }

    // endregion RESULT

    //endregion TEST STEP


    // Test Step
    public static int iTestStep;
    public static int lastTestStep;
    public static String sActionKeyword;
    public static int  paramCount;
    public static Object[]  param;
    public static String params;
    public static String result = Constanst.PASS;
    public static String error;
    public static String dataSet;
    public static String expected;
    public static String actual;
    public static String process;
    public static String description;
    public static String testStep;

    //Class
    public static KeyWords keyWord;
    public static Method method[];

    //Scope
    public static String sRunMode;
    public static String scopePath;
    public static String sTestCaseID;

    //Testcase
    public static String tcResult;
    public static String tcPath;
}
