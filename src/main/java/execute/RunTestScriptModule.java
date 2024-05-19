package execute;

import report.GenerateReport;

import java.lang.reflect.Method;

public class RunTestScriptModule extends TestScrip{
    public RunTestScriptModule( Method method[]){
        super(method);
    }
    public static void run(String scopePath,int iTestSuit,int iTotalSuite) throws Exception {
        getLevelFolder(1);
       execute_suites(scopePath,iTestSuit);
       GenerateReport.countResultPlan(scopePath,iTotalSuite);
    }
}
