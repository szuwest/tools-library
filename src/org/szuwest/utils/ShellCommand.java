package org.szuwest.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import my.base.util.LogUtils;

public class ShellCommand {
	private static final String TAG = "ShellCommand";
	public Shell sh;
	public Shell su;
	public boolean isRooted = false;

	public ShellCommand() {
		Shell localSH1 = new Shell("sh");
		this.sh = localSH1;
		Shell localSH2 = new Shell("su");
		this.su = localSH2;
	}

	public boolean canSU() {
		CommandResult result = this.su.runWaitFor("id");
		String msg = "canSU() su[" + result.exit_value + "]: " + result.stdout + result.stderr;
		LogUtils.d(TAG, msg);
		isRooted = result.success();
		return isRooted;
	}

	public Shell suOrSH() {
		boolean bool = canSU();
		Shell localSH;
		if (bool)
			localSH = this.su;
		else
			localSH = this.sh;
		return localSH;
	}

	public class CommandResult {
		public int exit_value;
		public String stderr;
		public String stdout;

		CommandResult(int initValue) {
			this(initValue, null, null);
		}

		CommandResult(int initValue, String stdout, String stderr) {
			this.exit_value = initValue;
			this.stdout = stdout;
			this.stderr = stderr;
		}

		public boolean success() {
			if (exit_value == 0)
				return true;
			else
				return false;
		}
	}

	public class Shell {
		private String SHELL = "sh";

		public Shell(String arg2) {
			this.SHELL = arg2;
		}

		private String getStreamLines(InputStream paramInputStream) {
			BufferedReader reader = new BufferedReader( new InputStreamReader(paramInputStream) );
		    int read;
		    char[] buffer = new char[4096];
		    StringBuffer output = new StringBuffer();
		    try {
				while ((read = reader.read(buffer)) > 0) {
				    output.append(buffer, 0, read);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return output.toString();
		}

		private Process run(String command) {
			Process localProcess = null;
			String str1 = this.SHELL;
			String str2 = "exec " + command + "\n";
			LogUtils.d(TAG, str1 +" "+ str2);
			try {
				Runtime localRuntime = Runtime.getRuntime();
				localProcess = localRuntime.exec(str1);
				OutputStream localOutputStream = localProcess.getOutputStream();
				DataOutputStream localDataOutputStream = new DataOutputStream(
						localOutputStream);
				localDataOutputStream.writeBytes(str2);
				localDataOutputStream.flush();
			} catch (Exception localException) {
				LogUtils.e(TAG, localException.getLocalizedMessage());
			}
			return localProcess;
		}

		public ShellCommand.CommandResult runWaitFor(String paramString) {
			Process localProcess = run(paramString);
			int exitValue = -1;
			String stdout = null;
			String stderr = null;
			try {
				exitValue = localProcess.waitFor();
				InputStream localInputStream1 = localProcess.getInputStream();
				stdout = getStreamLines(localInputStream1);
				InputStream localInputStream2 = localProcess.getErrorStream();
				stderr = getStreamLines(localInputStream2);
				LogUtils.d(TAG, "stdout="+stdout + " \nstderr="+stderr + "\nexitValue="+exitValue);
			} catch (InterruptedException localInterruptedException) {
				exitValue = -1;
				String str4 = localInterruptedException.toString();
				LogUtils.e(TAG, str4);
			} catch (NullPointerException localNullPointerException) {
				String str5 = localNullPointerException.toString();
				LogUtils.e(TAG, str5);
				exitValue = -1;
			}
			return new ShellCommand.CommandResult(exitValue, stdout, stderr);
		}
	}
}