package common.keywords.app;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.utility.*;
import execute.RunTestScriptData;
import execute.TestScrip;
import io.restassured.response.Response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static common.keywords.app.KeyWordsToAction.*;

public class KeyWordCustomForAISpeak {

    public static void returnChooseTopic(String sheetName, String from, String to,String exception, String part) throws IOException {
        try {
            String result = Constanst.FAIL;
            /*try {
                result  = KeyWordsToActionToVerify.isElementDisplay(locator);
            }catch (Throwable e){
                result = "false";
            }*/
            if(part.equals("1")){
                result = Constanst.TRUE;
            }
            String path = TestScrip.reportPath;
            if (result.equals(Constanst.TRUE)) {
                ExcelUtils.setCellData(Constanst.NO, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            } else {
                ExcelUtils.setCellData(Constanst.YES, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            }
            for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
                if (result.equals(Constanst.TRUE)) {
                    ExcelUtils.setCellData(Constanst.YES, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
                } else {
                    ExcelUtils.setCellData(Constanst.NO, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
                }
            }
            ExcelUtils.closeFile(path);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void returnChooseTopic(String sheetName, String from, String to, String part) throws IOException {
        try {
            String result;
            if(part.equals("1")){
                result = Constanst.TRUE;
            } else {
                result = Constanst.FAIL;
            }
            String path = TestScrip.reportPath;
                try {
                    for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
                        if (result.equals(Constanst.TRUE)) {
                            ExcelUtils.setCellData(Constanst.YES, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
                        } else {
                            ExcelUtils.setCellData(Constanst.NO, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
                        }
                    }
                }catch (Exception e){

                }
            ExcelUtils.closeFile(path);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void returnChooseTopic( String part) {
        try {
            String result = Constanst.FAIL;
            if(part.equals("1")){
                result = Constanst.TRUE;
            }
            String variable = FileHelpers.readFile(Constanst.VARIABLE_PATH_FILE);
            String value = "";
            if(variable.equals("")){
                variable = "{'"+Constanst.RUN_MODE_TC_VAR+"':'"+result+"'}";
            }else {
                variable = variable.replace("}","");
                variable += ",\n'"+Constanst.RUN_MODE_TC_VAR+"':'"+result+"'}";
            }
            FileHelpers.writeFile(variable,Constanst.VARIABLE_PATH_FILE);
            ExcelUtils.closeFile(Constanst.VARIABLE_PATH_FILE);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void setRunModeTC(String from, String to,String sheetName) throws IOException {
        String result = FileHelpers.getValueVariableFile(Constanst.RUN_MODE_TC_VAR);
        for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
            if (result.equals(Constanst.TRUE)) {
                ExcelUtils.setCellData(Constanst.YES, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            } else {
                ExcelUtils.setCellData(Constanst.NO, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            }
        }
        ExcelUtils.closeFile(TestScrip.reportPath);
    }

    public static void deFindModeRunTestCase(String key,String sheetName,String from, String to)  {
        try{
            ExcelUtils.setExcelFile(TestScrip.reportPath);
            ExecutorService executor = Executors.newFixedThreadPool(Integer.valueOf(to) - Integer.valueOf(from));
            String result = getDeFind(key);
            for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
                    Runnable runnable = new InsertMultiExcel(result, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
                    executor.execute(runnable);
            }
            executor.shutdown();
            ExcelUtils.saveFile(TestScrip.reportPath);
            ExcelUtils.closeFile(TestScrip.reportPath);
        }catch (Exception e){
            Log.error("deFindModeRunTestCase: "+ e.getMessage());
        }
    }
   /* public static void deFindModeRunTestCase(String key,String sheetName,String from, String to)  {
        try{
            String result = getDeFind(key);
            for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
                ExcelUtils.setCellData(result, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            }
        }catch (Exception e){
            Log.error("deFindModeRunTestCase: "+ e.getMessage());
        }
    }*/
    public static void deFindModeRunTestCase(String key,String sheetName,String from, String to,String exception)  {
        String result = getDeFind(key);
        if(result.equals(Constanst.YES)){
            ExcelUtils.setCellData(Constanst.NO, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.YES, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }
        for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
            ExcelUtils.setCellData(result, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }
    }
    public static void deFindModeRunTestCase(String key,String sheetName,String from, String to,String exception,String... exception1)  {
        String result = getDeFind(key);
        if(result.equals(Constanst.YES)){
            ExcelUtils.setCellData(Constanst.NO, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.YES, Integer.valueOf(exception), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            for (String ex:exception1) {
                ExcelUtils.setCellData(Constanst.NO, Integer.valueOf(ex), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            }
        }
        for (int i = Integer.valueOf(from); i <= Integer.valueOf(to); i++) {
            ExcelUtils.setCellData(result, i, Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }
    }
    private static String getDeFind(String key){
        String result = Constanst.NO;
        try {
            JsonObject jsonObject = JsonHandle.getObject(RunTestScriptData.json);
            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry: entries) {
                if(entry.getKey().equals(key)){
                    result = Constanst.YES;
                    break;
                }
            }
        }catch (Exception e){
            result = Constanst.NO;
            Log.error("getDeFind "+e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    public static void returnModeTC(String sheetName, String to, String expected, String part) {
        try {
            String result = Constanst.TRUE;
            boolean compare = false;
            /*try {
                result  = KeyWordsToActionToVerify.isElementDisplay(locator);
            }catch (Throwable e){
                result = "false";
            }*/
            if(part.length()>=expected.length()){
                compare = true;
            }
            if(compare==true) {
                if (part.contains(expected)) {
                    result = Constanst.FAIL;
                }
            }else {
                if (expected.contains(part)) {
                    result = Constanst.FAIL;
                }
            }
            String path = TestScrip.reportPath;
            if (result.equals(Constanst.TRUE)) {
                ExcelUtils.setCellData(Constanst.YES, Integer.parseInt(to), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            } else {
                ExcelUtils.setCellData(Constanst.NO, Integer.parseInt(to), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
            }
            ExcelUtils.closeFile(path);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void ignoreScript(String number,String to,String sheetName, String text){
        if(text.length()<=Integer.valueOf(number)){
            ExcelUtils.setCellData(Constanst.NO, Integer.parseInt(to), Constanst.RUN_MODE_TEST_CASE, sheetName, TestScrip.reportPath);
        }
    }

    public static String getGame() {
        return "Report_";
    }
    public static void changeModeTC(String methodName,String locator, String component,String tcRow,String expect) throws InvocationTargetException, IllegalAccessException {
        String level = runMethod(methodName,locator,component);
        String expected = expect;
        if(expected.contains("$.")) {
            expected = JsonHandle.getValue(RunTestScriptData.json, expect);
        }
        if(level.toLowerCase().contains(expected.toLowerCase())){
            ExcelUtils.setCellData(Constanst.NO,Integer.parseInt(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.YES,Integer.parseInt(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }
    }

    public static void changeModeTC(String variableKey,String tcNotExpRow,String tcExpRow,String expect) {

        String value = FileHelpers.getValueVariableFile(variableKey);
        if(value.equals(expect)){
            ExcelUtils.setCellData(Constanst.NO,Integer.valueOf(tcNotExpRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
            ExcelUtils.setCellData(Constanst.YES,Integer.valueOf(tcExpRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.YES,Integer.valueOf(tcNotExpRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
            ExcelUtils.setCellData(Constanst.NO,Integer.valueOf(tcExpRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }
    }
    public static void changeModeTCSetTrue(String actual,String tcRow,String expect) {
        String expected = JsonHandle.getValue(RunTestScriptData.json,expect);
        String actual1 = actual;
        if(actual1.contains("$.")) {
            if (actual1.contains("$.index")) {
                actual1 = FileHelpers.getValueVariableFile("$.index");
            } else {
                actual1 = JsonHandle.getValue(RunTestScriptData.json,actual);
            }
        }
        if(actual1.toLowerCase().equals(expected.toLowerCase())){
            ExcelUtils.setCellData(Constanst.YES,Integer.valueOf(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.NO,Integer.valueOf(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }
    }
    public static void changeModeTCSetFAIL(String actual,String tcRow,String expect) {
        String expected = JsonHandle.getValue(RunTestScriptData.json,expect);
        String actual1 = actual;
        if(actual1.contains("$.")) {
            if (actual1.contains("$.index")) {
                actual1 = FileHelpers.getValueVariableFile("$.index");
            } else {
                actual1 = JsonHandle.getValue(RunTestScriptData.json,actual);
            }
        }
        if(actual1.toLowerCase().equals(expected.toLowerCase())){
            ExcelUtils.setCellData(Constanst.NO,Integer.valueOf(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }else {
            ExcelUtils.setCellData(Constanst.TRUE,Integer.valueOf(tcRow),Constanst.RUN_MODE_TEST_CASE, Constanst.TESTCASE_SHEET, TestScrip.reportPath);
        }
    }
    private static String runMethod(String methodName,String locator, String component) throws InvocationTargetException, IllegalAccessException {
        Object result = new Object();
        Method method[];
        KeyWordsToActionToVerify keyWord = new KeyWordsToActionToVerify();
        method = keyWord.getClass().getMethods();
        for (int i = 0; i < method.length; i++) {
            if(method[i].getName().equals(methodName)){
                String type = method[i].getReturnType().getName();
                System.out.println(type);
                if(type.equals("java.lang.String")) {
                    result = method[i].invoke(keyWord, locator,component);
                }else {
                    continue;
                }
                break;
            }
        }
        System.out.println(result);
        return result.toString();
    }
    public static void swipeMap(String locator,String component, String property,String key,String level,String expect){
        Response response = request(Constanst.SCENE_URL,"//"+locator+"."+component);
        if(response !=null) {
            String value = convert(response, property);
            String json = FileHelpers.readFile(FileHelpers.getRootFolder() + FileHelpers.getValueConfig(key));
            List<Object> topic =JsonHandle.getJsonArray(json,"$.lvs[?(@.level=='"+level+"')].category[*].topic[*].name").toList();
            if(topic.contains((Object) value)){
                int actualIndex = LogicHandle.getIndexInList(topic,value);
                int expectIndex = LogicHandle.getIndexInList(topic,expect);
                if(actualIndex>expectIndex){
                    Log.info("swipe from right to left (1)");
                    swipe("255","270","500",String.valueOf(actualIndex-expectIndex));
                }else {
                    Log.info("swipe from left to right (-1)");
                    swipe("270","255","500",String.valueOf(expectIndex-actualIndex));
                }
            }
        }
    }
    public static void skipLesson(String locator){
        if(KeyWordsToActionToVerify.isElementDisplay(locator)==true){
            KeyWordsToAction.click(locator,"Button","onClick()");
            TestScrip.onFail("BẠN VẪN Ở TRONG LUỒNG LESSON");
        }
    }
}
