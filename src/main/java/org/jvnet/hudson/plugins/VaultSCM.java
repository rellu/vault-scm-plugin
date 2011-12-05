package org.jvnet.hudson.plugins;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;

@Extension
public final class VaultSCM extends SCM {
	
	public static class VaultSCMDescriptor extends
	SCMDescriptor<VaultSCM> {
	
	
		/**
		 * Constructor for a new VaultSCMDescriptor.
		 */
		protected VaultSCMDescriptor() {
			super(VaultSCM.class, null);
			load();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDisplayName() {
			return "Vault SCM";
		}
		
		@Override
		public SCM newInstance(StaplerRequest req, JSONObject formData)
				throws FormException {
			VaultSCM scm = req.bindJSON(VaultSCM.class, formData);
			return scm;
		}

	}
	
	//configuration variables from user interface

	private String server;	
	private String userName;
	private String password;
	private String repository; //name of the repository
	private String vaultSCMExecutable;
	private String path; //path in repository. Starts with $ sign.
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	//getters and setters
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	public String getVaultSCMExecutable() {
		return vaultSCMExecutable;
	}
	public void setVaultSCMExecutable(String vaultSCMExecutable) {
		this.vaultSCMExecutable = vaultSCMExecutable;
	}
	
	/**
	 * Singleton descriptor.
	 */
	@Extension
	public static final VaultSCMDescriptor DESCRIPTOR = new VaultSCMDescriptor();
	
	//TODO: verify dateformat from version history
	public static final SimpleDateFormat VAULT_DATETIME_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

	@DataBoundConstructor
	public VaultSCM(String server, String path, String userName,
			String password, String repository, String vaultSCMExecutable) {
		this.server = server;
		this.userName = userName;
		this.password = password;
		this.repository = repository;
		this.vaultSCMExecutable = vaultSCMExecutable;
		this.path = path;
	}
	
    @Override
    public SCMDescriptor<?> getDescriptor() {
        return DESCRIPTOR;
    }
    
    //TODO: method is incomplete
	@Override
	public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build,
			Launcher launcher, TaskListener listener) throws IOException,
			InterruptedException {

		//TODO: Class for Vault SCM Revision State
		VaultSCMRevisionState scmRevisionState = new VaultSCMRevisionState();		
		final Date lastBuildDate = build.getTime();
		scmRevisionState.setDate(lastBuildDate);
				
		return scmRevisionState;
	}
	
	@Override
	/* 
	 */
	protected PollingResult compareRemoteRevisionWith(
			AbstractProject<?, ?> project, Launcher launcher,
			FilePath workspace, TaskListener listener, SCMRevisionState baseline)
			throws IOException, InterruptedException {
		
		Date lastBuild = ((VaultSCMRevisionState)baseline).getDate();
		Date now = new Date();
		File temporaryFile = File.createTempFile("changes", "txt");
		
				
//		if (countChanges == 0)
//			return PollingResult.NO_CHANGES;
//		else if (countChanges < changesThreshold)
//			return PollingResult.SIGNIFICANT;
//			
		return PollingResult.BUILD_NOW;
	}
	
	
	@Override
	public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher,
			FilePath workspace, BuildListener listener, File changelogFile)
			throws IOException, InterruptedException {
		 boolean returnValue = true;
		 
		 if (server != null )
			 listener.getLogger().println("server: "+server);
		
		String[] cmd = new String[9];
		//required parameters
		cmd[0] = getVaultSCMExecutable();
		cmd[1] = "GET" ;
		cmd[2] = "-host ".concat(server) ;
		cmd[3] = "-ssl ";
		cmd[4] = "-user ".concat(userName);
		cmd[5] = "-password ".concat(password);
		cmd[6]= "-repository ".concat(repository);
		//optional parameters
		cmd[7]= "-merge overwrite ";
		cmd[8]= path;
		int cmdResult = launcher.launch().cmds(cmd).envs(new String[0])
				.stdin(null).stdout(listener.getLogger()).pwd(workspace).join();
		if (cmdResult == 0)
		{
			final Run<?, ?> lastBuild = build.getPreviousBuild();
			final Date lastBuildDate;

			if (lastBuild == null) {
				lastBuildDate = new Date();
				lastBuildDate.setTime(0); // default to January 1, 1970
				listener.getLogger().print("Never been built.");				
			} else
				lastBuildDate = lastBuild.getTimestamp().getTime();
			
			Date now = new Date(); //defaults to current
			
			returnValue = captureChangeLog(launcher, workspace,listener, lastBuildDate, now, changelogFile);
		}
		else
			returnValue = false;
			
		listener.getLogger().println("Checkout completed.");	
		return returnValue;
	}

	@Override
	public ChangeLogParser createChangeLogParser() {		
		return new VaultSCMChangeLogParser();
	}

	private boolean captureChangeLog(Launcher launcher, FilePath workspace,
			BuildListener listener, Date lastBuildDate, Date currentDate, File changelogFile) throws IOException, InterruptedException {
		
		boolean result = true;
		
		String dateRange = VAULT_DATETIME_FORMATTER.format(lastBuildDate);
		dateRange = dateRange.concat(":");
		dateRange = dateRange.concat(VAULT_DATETIME_FORMATTER.format(currentDate));		
		
		String[] cmd = new String[8];
		//required parameters
		cmd[0] = getVaultSCMExecutable();
		cmd[1] = "VERSIONHISTORY " ;
		cmd[2] = "-host ".concat(server);
		cmd[3] = "-ssl ";
		cmd[4] = "-user ".concat(userName);
		cmd[5] = "-password ".concat(password);
		cmd[6]= "-repository ".concat(repository);
		cmd[7] = path;
		//optional parameters here if needed
		
		FileOutputStream os = new FileOutputStream(changelogFile);
		try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            PrintWriter writer = new PrintWriter(new FileWriter(changelogFile));
            try {            	
            	
            	
            	int cmdResult = launcher.launch().cmds(cmd).envs(new String[0]).stdin(null).stdout(bos).pwd(workspace).join();
            	if (cmdResult != 0)
            	{
            		listener.fatalError("Changelog failed with exit code " + cmdResult);
            		result = false;
            	}
            	
            	
            } finally {
            	writer.close();
                bos.close();
            }
        } finally {
            os.close();
        }

        listener.getLogger().println("Changelog calculated successfully.");
        listener.getLogger().println("Change log file: " + changelogFile.getAbsolutePath() );
        
        return result;
	}
	
	
	
	
    
    

}
