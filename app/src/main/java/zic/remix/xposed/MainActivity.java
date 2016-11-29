package zic.remix.xposed;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import zic.remix.xposed.*;
import android.sax.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		final Button installBtn = (Button) findViewById(R.id.mainButton);
		final TextView outputText = (TextView) findViewById(R.id.outputText);
		final Button rebootButton = (Button) findViewById(R.id.rebootButton);
		
		rebootButton.setOnClickListener(new View.OnClickListener() {
			@Override
				public void onClick(View v) {
					CmdExecuter exe1 = new CmdExecuter();
				exe1.Excuter("setprop ctl.restart zygote\nsetprop ctl.restart zygote");
				}
		});
		installBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				firstRun();
				CmdExecuter exe = new CmdExecuter();
				String textValue = "Result:\n\n";
				exe.Excuter("cd /data/data/zic.remix.xposed/Zickie/xposed\nsh zic-flash-script.sh");
				//textValue += exe.error + exe.output;
				outputText.setText(textValue);
				outputText.setVisibility(View.VISIBLE);
				if(exe.output.endsWith("Done")) {
					installBtn.setTextColor(Color.parseColor("#8BC34A"));
					installBtn.setText("Succeed!");
					textValue += "Installed Suceessfully!\nMake sure you have installed Xposed Installer and press Hot Reboot";
					outputText.setText(textValue);
				}else if(exe.error.endsWith("denied")) {
					installBtn.setTextColor(Color.parseColor("#FF5722"));
					installBtn.setText("Failed!");
					textValue += "Error\nPlease allow the root request and try again";
					outputText.setText(textValue);
				}else if(exe.output.endsWith("file_error")) {
					installBtn.setTextColor(Color.parseColor("#FF5722"));
					installBtn.setText("Failed!");
					textValue += "Error\nThe resources has been damamged, please clear App's Data in Settings and try again";
					outputText.setText(textValue);
				}else if(exe.output.endsWith("mount_error")) {
					installBtn.setTextColor(Color.parseColor("#FF5722"));
					installBtn.setText("Failed!");
					textValue += "Error\nCannot mount system, please extract Zickie Remix REMOUNT RW.exe to RemixOS installation directory and try again";
					outputText.setText(textValue);
				}else if(exe.output.endsWith("invalid_error")) {
					installBtn.setTextColor(Color.parseColor("#FF5722"));
					textValue += "Error\nSorry! This script is invalid for this Remix OS version.";
					installBtn.setText("Failed!");
					outputText.setText(textValue);
				}
			}
			});
	}
	
	private boolean firstRun() {
		SharedPreferences prefs = getSharedPreferences("first_run", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            Toast.makeText(MainActivity.this, "First Run - Checking Necessary File!", Toast.LENGTH_LONG).show();
			copyFileOrDir("Zickie");
            prefs.edit().putBoolean("firstrun", false).commit();
			return true;
        }else {
			return false;
		}
    }
	
	private void copyFileOrDir(String path) {
		AssetManager assetManager = this.getAssets();
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets.length == 0) {
				copyFile(path);
			} else {
				String fullPath = "/data/data/" + this.getPackageName() + "/" + path;
				File dir = new File(fullPath);
				if (!dir.exists())
					dir.mkdir();
				for (int i = 0; i < assets.length; ++i) {
					copyFileOrDir(path + "/" + assets[i]);
				}
			}
		} catch (IOException ex) {
			Log.e("tag", "I/O Exception", ex);
		}
	}

	private void copyFile(String filename) {
		AssetManager assetManager = this.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String newFileName = "/data/data/" + this.getPackageName() + "/" + filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

}
