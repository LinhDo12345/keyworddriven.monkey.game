package execute;

import common.utility.Constanst;
import common.utility.ExcelUtils;
import common.utility.FileHelpers;
import common.utility.Log;

import java.io.IOException;
import java.util.Arrays;

public class EndTestScript {
    public static void saveReportToFailListFile(String tcPath,String scope){
        try {
            ExcelUtils.setExcelFile(tcPath);
            int fail =0;
            int pass = 0;
            int total = ExcelUtils.getRowCount(Constanst.TESTCASE_SHEET);
            for (int i = 0;i<total;i++){
                if (ExcelUtils.getStringValueInCell(i,Constanst.TESTCASE_STATUS,Constanst.TESTCASE_SHEET).equals(Constanst.FAIL)){
                    fail ++;
                    break;
                }
            }
            String file = "";
            if (fail>0) {
                Log.info("Save file fail to file: report\\list_fail.txt");

                for (String name : Arrays.stream(tcPath.split("\\\\")).toList()) {
                    if (name.contains(".xlsx")) {
                        file = name.replace(".xlsx", "");
                    }
                }

                String failPath = Constanst.LIST_FAIL_PATH_FILE+"list_fail.txt";
                FileHelpers.createFile(failPath);
                String content = FileHelpers.readFile(failPath);

                if (!content.equals("")) {
                    boolean contain = false;
                    if(content.contains(",")){
                        if(Arrays.stream(content.split("\\,")).toList().contains(file)){
                            contain = true;
                        }
                    }
                    if(contain==false) {
                        content = content + ",\n" + file;
                    }
                } else {
                    content = file;
                }
                FileHelpers.writeFile(content, failPath);

            }else {
                pass =1;
            }

            sumResultToScope(pass,fail,scope);
        }catch (Exception e){
            Log.error(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void sumResultToScope(int pass,int fail,String path) throws IOException {
        ExcelUtils.setExcelFile(path);
        int scopePass =ExcelUtils.getNumberValueInCell(1,Constanst.PASS_PLAN_COLUM,Constanst.PLAN_SHEET);
        int scopeFail = ExcelUtils.getNumberValueInCell(1,Constanst.FAIL_PLAN_COLUM,Constanst.PLAN_SHEET);;
        if(pass>0){
           scopePass ++;
        }
        if (fail>0){
            scopeFail ++;
        }
        ExcelUtils.setCellData(scopePass,1,Constanst.PASS_PLAN_COLUM,Constanst.PLAN_SHEET,path);
        ExcelUtils.setCellData(scopeFail,1,Constanst.FAIL_PLAN_COLUM,Constanst.PLAN_SHEET,path);
        ExcelUtils.closeFile(path);
    }
}