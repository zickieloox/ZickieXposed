package zic.remix.xposed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Zickie Loox on 11/24/2016.
 * Based on http://forum.xda-developers.com/showthread.php?t=2226664
 */

public class CmdExecuter {
    public String output, error;
    boolean executable = false;
	
    public Boolean Excuter(String cmd) {
        StringBuffer out = new StringBuffer();
        StringBuffer err = new StringBuffer();
        try {
            String line;
            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();

            stdin.write((cmd+"\n").getBytes());
			stdin.write("exit\n".getBytes());
            stdin.flush();

            stdin.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                out.append(line + "\n");
            }
            br.close();

            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                err.append(line + "\n");
            }
            br.close();

            process.waitFor();
            process.destroy();

        } catch (Exception ex) {
        }
        output = "Output:\n" + out.toString().trim(); //Trim to remove \n
        error = "Error:\n" + err.toString().trim();

        //Check command is executable?
        if(err != null) {
            executable = false;
        }else {
            executable = true;
        }
        return executable;
    }
}

