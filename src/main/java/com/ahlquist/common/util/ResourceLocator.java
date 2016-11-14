package com.ahlquist.common.util;

public class ResourceLocator {
	private static String server_home = null;
	private static String war_dir = null;
	private static String database_properties = null;
	private static String email_properties = null;
	private static String sysparam_properties = null;
	private static String install_properties = null;

	static {
		server_home = System.getProperty("server_home");
		war_dir = System.getProperty("war_dir");

		if (server_home == null || war_dir == null) {
			if (server_home == null) {
				System.out.println("server_home variable not set in evironment");
			}
			if (war_dir == null) {
				System.out.println("war_dir variable not set in evironment");
			}
			System.exit(0);
		}

		database_properties = new String(server_home + "/database.properties");
		email_properties = new String(server_home + "/email.properties");
		sysparam_properties = new String(server_home + "/SysParam.properties");
		install_properties = new String(server_home + "/install.properties");
	}

	public static String getLogDir() {
		return System.getProperty("log_dir");
	}

	public static String getInstallProperties() {
		return (install_properties);
	}

	public static String getSysParamProperties() {
		return (sysparam_properties);
	}

	public static String getDatabaseProperties() {
		return (database_properties);
	}

	public static String getEmailProperties() {
		return (email_properties);
	}

	public static void main(String[] args) {
		System.out.println("database.properties = " + getDatabaseProperties());
		System.out.println("email.properties    = " + getEmailProperties());
		System.out.println("SysParam.properties = " + getSysParamProperties());
	}

}