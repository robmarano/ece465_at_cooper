package edu.cooper.ece465.apps.imaging;

public enum ImagingAppExitCode {
	SUCCESS (0, "Graceful, successful execution exit"),
	FAIL_GENERIC (1, "Generic execution error"),
	INCORRECT_CLI_ARGS (2, "Incorrect CLI arguments"),
	UNABLE_LISTEN_PORT (3, "Unable to listen in on port");

	private final int errorCode;
	private final String errorDescription;

	public int getErrorCode() {
		return this.errorCode;
	}

	public String getErrorDescription() {
		return this.errorDescription;
	}

	ImagingAppExitCode(int errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this. errorDescription = errorDescription;
	}
}