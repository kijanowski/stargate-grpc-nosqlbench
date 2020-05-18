package io.nosqlbench.virtdata.userlibs.apps;

import io.nosqlbench.virtdata.userlibs.apps.diagnoseapp.VirtDataDiagnoseApp;
import io.nosqlbench.virtdata.userlibs.apps.docsapp.VirtDataGenDocsApp;
import io.nosqlbench.virtdata.userlibs.apps.valuesapp.VirtDataCheckPerfApp;

import java.util.Arrays;

/**
 * This just routes the user to the correct sub-app depending on the leading verb, stripping it off in the process.
 */
public class VirtDataMainApp {

    private final static String APP_TESTMAPPER = "testmapper";
    private final static String APP_GENDOCS = "gendocs";
    private final static String APP_DIAGNOSE = "diagnose";
    private final static String[] names = new String[]{APP_GENDOCS, APP_TESTMAPPER, APP_DIAGNOSE};

    public static boolean hasNamedApp(String appname) {
        return (appname.equals(APP_TESTMAPPER) || appname.equals(APP_GENDOCS) || appname.equals(APP_DIAGNOSE));
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: app (" + APP_TESTMAPPER + "|" + APP_GENDOCS + "|" + APP_DIAGNOSE +")");
            System.exit(0);
        }

        String appSelection = args[0];
        String[] appArgs = new String[0];
        if (args.length > 1) {
            appArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        if (appSelection.toLowerCase().equals(APP_TESTMAPPER)) {
            VirtDataCheckPerfApp.main(appArgs);
        } else if (appSelection.toLowerCase().equals(APP_GENDOCS)) {
            VirtDataGenDocsApp.main(appArgs);
        } else if (appSelection.toLowerCase().equals(APP_DIAGNOSE)) {
            VirtDataDiagnoseApp.main(appArgs);
        } else {
            System.err.println("Error in command line. The first argument must one of " + String.join(",", names));
        }
    }
}
